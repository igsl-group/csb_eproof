package com.hkgov.csb.eproof.event;

import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.service.EmailService;
import hk.gov.spica_scopes.common.client.PropertyNames;
import hk.gov.spica_scopes.common.jaxb.ScopesFault;
import hk.gov.spica_scopes.spica.jaxb.notisender.Attachment;
import hk.gov.spica_scopes.spica.jaxb.notisender.NotiStatus;
import hk.gov.spica_scopes.spica.jaxb.notisender.Recipient;
import hk.gov.spica_scopes.spica.notification.client.restful.NotificationRestfulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class EmailEventListener {
    private static final Logger logger =
            LoggerFactory.getLogger(EmailEventListener.class);
    private final EmailService emailService;
    private final EmailEventRepository emailEventRepository;
    @Value("${spring.mail.env}")
    private String env;
    // TBC
    @Value("${gcis-shared-service.property.path}")
    private String propertyFilePath;
    // TBC
    @Value("${gcis-shared-service.keystore.path}")
    private String keyStoreFilePath;
    @Value("${gcis-shared-service.keystore.password}")
    private String keyStoreFilePassword;
    @Value("${gcis-shared-service.keystore.alias}")
    private String keyStoreFileAlias;
    @Value("${gcis-shared-service.emailWhitelist.enabled}")
    private Boolean  whitelistEnabled;
    @Value("${gcis-shared-service.emailWhitelist.toList}")
    private String[] whiteList;
    @Value("${gcis-shared-service.notiSvc.endPointName}")
    private String endpointName;
    @Value("${gcis-shared-service.notiSvc.endPointUrl}")
    private String endpointUrl;

    @Value("${gcis-shared-service.enabled:false}")
    private boolean serviceEnabled;
    public EmailEventListener(EmailService emailService,
            EmailEventRepository emailEventRepository) {
        this.emailService = emailService;
        this.emailEventRepository = emailEventRepository;
    }

    @Async
    @EventListener
    public void handleAsyncEmailEvent(EmailEvent event) {
        try {
            if (serviceEnabled) {
                sendEmailNotification(event);
                event.setStatus("SUCCESS");
            }
        } catch (Exception e) {
            logError(e, event);
            event.setStatus("FAILED");
        } finally {
            emailEventRepository.save(event);
        }
    }

    private void sendEmailNotification(final EmailEvent event) throws Exception {
        try (Response response = initializeAndSendNotification(event)) {
            processNotificationResponse(response, event);
        }
    }

    private Response initializeAndSendNotification(final EmailEvent event) throws Exception {
        NotificationRestfulClient client = initializeClient();
        Recipient[] recipients = prepareRecipients(event);
        return sendNotification(client, event, recipients);
    }

    private NotificationRestfulClient initializeClient() throws Exception {
        Properties properties = getSSLProperties(endpointName, endpointUrl);
        return new NotificationRestfulClient(properties);
    }

    private Recipient[] prepareRecipients(EmailEvent event) {
        List<String> toList = Arrays.stream(event.getEmailMessage().getTo().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        if (whitelistEnabled) {
            Set<String> whitelistSet = new HashSet<>(Arrays.asList(whiteList));
            toList = toList.stream()
                    .filter(whitelistSet::contains)
                    .collect(Collectors.toList());
        }

        return toList.stream()
                .map(this::createRecipient)
                .toArray(Recipient[]::new);
    }

    private Recipient createRecipient(String email) {
        Recipient recipient = new Recipient();
        recipient.setChanAddr(email);
        logger.info("[Email] Preparing recipient with address: {}", email);
        return recipient;
    }

    private Response sendNotification(NotificationRestfulClient client, EmailEvent event, Recipient[] recipients) throws Exception {
        final String chanType = "EM";
        final String charSet = "UTF-8";
        final String contentType = "text/html";
        String subject = event.getEmailMessage().getSubject();
        String content = event.getEmailMessage().getBody();

        // Notification flags
        final boolean isEncrypt = false;
        final boolean isSign = false;
        final boolean isRestricted = false;
        final boolean isUrgent = false;

        return client.sendNotificationRequest(
                chanType, charSet, contentType, subject, content.getBytes(),
                new Attachment[0], recipients, isEncrypt, isSign, isRestricted, isUrgent);
    }

    private void processNotificationResponse(Response response, EmailEvent event) throws Exception {
        int statusCode = response.getStatus();
        String responseBody = response.readEntity(String.class);
        logger.info("Notification response status code: {}", statusCode);
        logger.info("Notification response body: {}", responseBody);

        if (statusCode == Response.Status.OK.getStatusCode()) {
            logNotificationStatus(response);
        } else {
            handleFailedNotification(response);
        }
    }

    private void logNotificationStatus(Response response) throws Exception {
        List<NotiStatus> notiStatusList = getNotificationClient().getNotificationNotificationStatus(response);
        for (NotiStatus notiStatus : notiStatusList) {
            logger.info("Notification status - Channel Address: {}, Result Code: {}, Message: {}",
                    notiStatus.getChanAddr(), notiStatus.getResultCd(), notiStatus.getResultMesg());
        }
    }

    private void handleFailedNotification(Response response) throws Exception {
        ScopesFault scopesFault = getNotificationClient().getScopesFault(response);
        logger.error("Notification failed with Scopes Fault: {}", scopesFault.getDescription());
    }

    private void logError(Exception e, EmailEvent event) {
        String errorMsg = String.format(
                "Failed to handle async email event. EmailMessageId: %s. Error: %s",
                event.getEmailMessageId(), e.getMessage()
        );
        logger.error(errorMsg, e);
    }

    private Properties getSSLProperties(String endPointName, String endPointUrl) throws Exception {
        Properties propAuth = new Properties();
        propAuth.setProperty(PropertyNames.AUTH_TYPE_PROPERTY, PropertyNames.AUTH_SSL_CLIENT_X509_CERTIFICATE);
        propAuth.setProperty(PropertyNames.PROXIMITY_CONFIG_FILE_PROPERTY, propertyFilePath);
        propAuth.setProperty(PropertyNames.RETRY_ON_EXCEPTION_FLAG_PROPERTY, "Y");
        propAuth.setProperty(PropertyNames.KEY_STORE_FILE_NAME_PROPERTY, keyStoreFilePath);
        propAuth.setProperty(PropertyNames.KEY_STORE_PASSWORD_PROPERTY, keyStoreFilePassword);
        propAuth.setProperty(PropertyNames.KEY_STORE_TYPE_PROPERTY, PropertyNames.KEY_STORE_TYPE_PKCS12);
        propAuth.setProperty(PropertyNames.KEY_ALIAS_PROPERTY, keyStoreFileAlias);
        propAuth.setProperty(PropertyNames.KEY_ALIAS_PASSWORD_PROPERTY, keyStoreFilePassword);
        propAuth.setProperty(endPointName, endPointUrl);

        return propAuth;
    }

    private NotificationRestfulClient getNotificationClient() throws Exception {
        return initializeClient();
    }
}
