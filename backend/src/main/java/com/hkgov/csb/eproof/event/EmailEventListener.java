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
import java.io.File;
import java.util.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
    private String[] whitelistEnabled;
    @Value("${gcis-shared-service.emailWhitelist.toList}")
    private String[] toList;
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

    /*
     * @Async
     * 
     * @EventListener public void handleAsyncEmailEvent(EmailEvent event) { try { emailService.sendEmail(event); event.setStatus("SUCCESS"); } catch (Exception e) {
     * logger.error("handleAsyncEmailEvent Failed Pk:" + event.getEmailEventId() + " Reason:" + e.getMessage()); event.setStatus("FAILED"); } emailEventRepository.save(event); }
     */

    @Async
    @EventListener
    public void handleAsyncEmailEvent(EmailEvent event) {
        try {
            if (serviceEnabled) {
                logger.info("DoSend Entry Point");
                
                this.DoSend(event);
            } else {
                emailService.sendEmail(
                        Arrays.asList(event.getEmailMessage().getTo()),
                        Arrays.asList(event.getEmailMessage().getCc()),
                        Arrays.asList(event.getEmailMessage().getBcc()),
                        event.getEmailMessage().getSubject(),
                        event.getEmailMessage().getBody(),
                        event.getEmailMessage().getAttachment().getName(),
                        null);
            }
            event.setStatus("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("handleAsyncEmailEvent Failed Pk:"
            + event.getEmailMessageId() + " Reason:" + e.getMessage());
            event.setStatus("FAILED");
        }
        emailEventRepository.save(event);
    }

    public void DoSend(final EmailEvent event) {
        try {
            Properties prop = getSSLProperties(endpointName, endpointUrl);

            List<String> toList = new ArrayList<>();
            // List<String> files = new ArrayList<>();
            toList.add(event.getEmailMessage().getTo());
            // files.add("abc.pdf");
            // Create the NotificationRestfulClient object
            NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);

            // Prepare web services parameter value
            String chanType = "EM"; // Channel type
            String charSet = "UTF-8"; // Character set
            String contentType = "text/html"; // Character set
            String subject = event.getEmailMessage().getSubject();
            String content =  event.getEmailMessage().getBody();

            boolean isEncrypt = false;
            boolean isSign = false;
            boolean isRestricted = false;
            boolean isUrgent = false;

            List<Attachment> attachments = new ArrayList<>();
            Attachment attachment = new Attachment();
            boolean containsAttachments = false;

                 // // Prepare recipient detail array
            Recipient[] recipientDetailArray = new Recipient[0];
            logger.info("toList size: {}", toList.size());
            // numOfReceipient � no. of notification receipient
            if (toList != null && toList.size() > 0) {
                if (!"prod".equalsIgnoreCase(env)) {
                    List<Attachment> finalAttachments = attachments;
                    recipientDetailArray = toList.stream()
                            .filter(this::whitelistFilter).map(x -> {
                                Recipient recipient = new Recipient();
                                recipient.setChanAddr(x);
                                logger.info("[Email] email: {}", x);

                                recipient.getRecipientAtthFile()
                                        .addAll(finalAttachments);
                                return recipient;
                            }).toList().toArray(new Recipient[0]);
                } else {
                    List<Attachment> finalAttachments = attachments;
                    recipientDetailArray = toList.stream().map(x -> {
                        Recipient recipient = new Recipient();
                        recipient.setChanAddr(x);
                        logger.info("[Email] email: {}", x);

                        recipient.getRecipientAtthFile()
                                .addAll(finalAttachments);
                        return recipient;
                    }).toList().toArray(new Recipient[0]);
                }

            }

            logger.info("[1]");
            Attachment[] attachmentArray = attachments.toArray(new Attachment[attachments.size()]);
            for (Attachment att : attachmentArray) {
                logger.info("- attachment file size: {}",
                        att.getFileContent().length);
            }
            logger.info("recipientDetailArray size: {}", recipientDetailArray.length);
            for (Recipient rec : recipientDetailArray) {
                logger.info("- recipient attachment list size: {}",
                        rec.getRecipientAtthFile().size());
                for (Attachment a : rec.getRecipientAtthFile()) {
                    logger.info("-- recipient attachment file size: {}",
                            a.getFileContent().length);
                }
            }

            // Create and call the Notification send service
            Response resp = notiRestfulClient.sendNotificationRequest(chanType,
                    charSet, contentType, subject, content.getBytes(),
                    new Attachment[0], recipientDetailArray, isEncrypt, isSign,
                    isRestricted, isUrgent);

            logger.info("[2]");
            logger.info("resp status code : " + resp.getStatus());
            logger.info("resp body: " + resp.readEntity(String.class));

            if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
                // String ResultCd = notiRestfulClient.getNotificationStatus(resp);
                // System.out.println(ResultCd);
                List<NotiStatus> notiStatusList = notiRestfulClient
                        .getNotificationNotificationStatus(resp);
                for (NotiStatus notiStatus : notiStatusList) {
                    logger.warn("ChanAddr: " + notiStatus.getChanAddr());
                    logger.warn("ResultCd: " + notiStatus.getResultCd());
                    logger.warn("ResultMsg: " + notiStatus.getResultMesg());
                }
            } else {
                ScopesFault scopesFault =
                        notiRestfulClient.getScopesFault(resp);
                logger.error("[Fail] {}", scopesFault.getDescription());
                // handle the soap fault
            }
        } catch (Exception e) {
            logger.error("[Exception Error]", e);
        }
    }

    private boolean whitelistFilter(String to) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        if (toList != null) {
            return Arrays.stream(toList)
                    .anyMatch(p -> pathMatcher.match(p, to));
        } else {
            return false;
        }
    }


    private Properties getSSLProperties(String endPointName, String endPointUrl)
            throws Exception {
        String propertyFile = propertyFilePath;
        String keyStoreFile = keyStoreFilePath;
        String keyStorePassword = keyStoreFilePassword;
        String keyStoreAlias = keyStoreFileAlias;

        Properties propAuth = new Properties();
        propAuth.setProperty(PropertyNames.AUTH_TYPE_PROPERTY, PropertyNames.AUTH_SSL_CLIENT_X509_CERTIFICATE);
        propAuth.setProperty(PropertyNames.PROXIMITY_CONFIG_FILE_PROPERTY, propertyFile);
        propAuth.setProperty(PropertyNames.KEY_STORE_FILE_NAME_PROPERTY, keyStoreFile);
        propAuth.setProperty(PropertyNames.KEY_STORE_PASSWORD_PROPERTY, keyStorePassword);
        propAuth.setProperty(PropertyNames.KEY_STORE_TYPE_PROPERTY, PropertyNames.KEY_STORE_TYPE_PKCS12);
        propAuth.setProperty(PropertyNames.KEY_ALIAS_PROPERTY, keyStoreAlias);
        propAuth.setProperty(PropertyNames.KEY_ALIAS_PASSWORD_PROPERTY, keyStorePassword);
        propAuth.setProperty(endPointName, endPointUrl);

        // Properties propAuth = new Properties();
        // propAuth.setProperty(PropertyNames.AUTH_TYPE_PROPERTY,
        //         PropertyNames.AUTH_SSL_CLIENT_X509_CERTIFICATE);
        // propAuth.setProperty(PropertyNames.PROXIMITY_CONFIG_FILE_PROPERTY,
        //         propertyFilePath);

        // propAuth.setProperty(PropertyNames.KEY_STORE_FILE_NAME_PROPERTY,
        //         keyStoreFilePath);
        // propAuth.setProperty(PropertyNames.KEY_STORE_PASSWORD_PROPERTY,
        //         keyStoreFilePassword);
        // propAuth.setProperty(PropertyNames.KEY_STORE_TYPE_PROPERTY,
        //         PropertyNames.KEY_STORE_TYPE_PKCS12);
        // propAuth.setProperty(PropertyNames.KEY_ALIAS_PROPERTY,
        //         keyStoreFileAlias);
        // propAuth.setProperty(PropertyNames.KEY_ALIAS_PASSWORD_PROPERTY,
        //         keyStoreFilePassword);

        // propAuth.setProperty(endPointName, endPointUrl);

        return propAuth;
    }
}
