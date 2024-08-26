package com.hkgov.csb.eproof.event;

// import hk.gov.spica_scopes.common.client.PropertyNames;
// import hk.gov.spica_scopes.common.jaxb.ScopesFault;
// import hk.gov.spica_scopes.common.utils.encoder.EncoderUtils;
// import hk.gov.spica_scopes.spica.jaxb.notisender.Attachment;
// import hk.gov.spica_scopes.spica.jaxb.notisender.NotiStatus;
// import hk.gov.spica_scopes.spica.jaxb.notisender.Recipient;
// import hk.gov.spica_scopes.spica.notification.client.restful.NotificationRestfulClient;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.service.EmailService;
import jakarta.mail.MessagingException;
import java.io.InputStream;
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
    @Value("${property.file.path}")
    private String propertyFilePath;

    // TBC
    @Value("${keystore.file.path}")
    private String keyStoreFilePath;

    @Value("${keystore.file.password}")
    private String keyStoreFilePassword;

    @Value("${keystore.file.alias}")
    private String keyStoreFileAlias;

    @Value("${spring.mail.whitelist}")
    private String[] whitelist;

    @Value("${gcis.notiSvc.endPointName}")
    private String endpointName;
    @Value("${gcis.notiSvc.endPointUrl}")
    private String endpointUrl;

    @Value("${mail.include.attachment}")
    private String includeAttachmentToMail;



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
            if (env.equals("uat") || env.equals("prod")) {
                //this.DoSend(event);
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

//     public void DoSend(final EmailEvent event) {
//         try {
//             Properties prop = getSSLProperties(endpointName, endpointUrl);

//             // List<String> files = new ArrayList<>();
//             List<String> toList = new ArrayList<>(Arrays
//                     .asList(event.getEmailMessage().getTo().split(";")));
//             // files.add("abc.pdf");
//             // Create the NotificationRestfulClient object
//             NotificationRestfulClient notiRestfulClient =
//                     new NotificationRestfulClient(prop);

//             // Prepare web services parameter value
//             String chanType = "EM"; // Channel type
//             String charSet = "UTF-8"; // Character set
//             String contentType = "text/html"; // Character set
//             String subject = "";
//             String content = "";
//             if (event.getEmailMessage().getIsCustom() != null
//                     && event.getEmailMessage().getIsCustom()) {
//                 subject = event.getEmailMessage().getCustomSubject();
//                 content = event.getEmailMessage().getCustomContent();
//             } else {
//                 subject =
//                         event.getEmailMessage().getEmailTemplate().getSubject();
//                 content = emailService.convertContextToHtmlBody(
//                         event.getEmailMessage().getEmailTemplate().getBody(),
//                         event.getEmailMessage().getContext(), Locale.ENGLISH);;
//             }

//             boolean isEncrypt = false;
//             boolean isSign = false;
//             boolean isRestricted = false;
//             boolean isUrgent = false;

//             List<Attachment> attachments = new ArrayList<>();
//             Attachment attachment = new Attachment();
//             boolean containsAttachments = ObjectUtils
//                     .isNotEmpty(event.getEmailMessage().getAttachment());
//             // if (containsAttachments) {



//                 /*
//                  * if (useSelfAttachment.equals("true")) { attachment.setFileContent(EncoderUtils.BASE64Encode(Files.readAllBytes(Paths.get(useSelfPath)))); attachment.setContentType(useSelfContentType);
//                  * attachment.setFileName(useSelfAttachmentFileName);
//                  * 
//                  * attachments.add(attachment);
//                  * 
//                  * 
//                  * } else {
//                  */
//             //     attachment.setFileName(
//             //             event.getEmailMessage().getAttachmentName());
//             //     try (InputStream inputStream = event.getEmailMessage()
//             //             .getAttachment().getInputStream()) {
//             //         attachment.setFileContent(inputStream.readAllBytes());
//             //     }
//             //     attachment.setContentType(event.getEmailMessage()
//             //             .getAttachment().getContentType());
//             //     attachments.add(attachment);
//             // }
                
//                 logger.info("[File 2] getFileName: {}", event.getEmailMessage().getAttachmentName());
//                 logger.info("[File 2] getContentType: {}", event.getEmailMessage().getAttachment().getContentType());
//                 try (InputStream inputStream = event.getEmailMessage().getAttachment().getInputStream()) {
//                 logger.info("[File 2] size: {}", EncoderUtils.BASE64Encode(inputStream.readAllBytes()).length);
//                 }
//             }

//             // Recipient[] recipientDetailArray = new Recipient[0];
//             // logger.info("toList size: {}", toList.size());
//             // // numOfReceipient � no. of notification receipient
//             // if (toList != null && toList.size() > 0) {
//             //     List<Attachment> finalAttachments = attachments;
//             //     if (!"prod".equalsIgnoreCase(env)) {
//             //         recipientDetailArray = toList.stream()
//             //                 .filter(this::whitelistFilter).map(x -> {
//             //                     Recipient recipient = new Recipient();
//             //                     recipient.setChanAddr(x);
//             //                     logger.info("[Email] email: {}", x);

//             //                     // recipient.getRecipientAtthFile().addAll(finalAttachments);
//             //                     return recipient;
//             //                 }).collect(Collectors.toList())
//             //                 .toArray(new Recipient[0]);
//             //     } else {
//             //         recipientDetailArray = toList.stream().map(x -> {
//             //             Recipient recipient = new Recipient();
//             //             recipient.setChanAddr(x);
//             //             logger.info("[Email] email: {}", x);

//             //             // recipient.getRecipientAtthFile().addAll(finalAttachments);
//             //             return recipient;
//             //         }).collect(Collectors.toList()).toArray(new Recipient[0]);
//             //     }
//             // }

//             // logger.info("[1]");
//             // Attachment[] attachmentArray =
//             //         attachments.toArray(new Attachment[attachments.size()]);
//             // logger.info("attachmentArray size: {}", attachmentArray.length);
//             // for (Attachment att : attachmentArray) {
//             //     logger.info("- attachment file size: {}",
//             //             att.getFileContent().length);
//             // }
//             // logger.info("recipientDetailArray size: {}",
//             //         recipientDetailArray.length);
//             // for (Recipient rec : recipientDetailArray) {
//             //     logger.info("- recipient attachment list size: {}",
//             //             rec.getRecipientAtthFile().size());
//             //     for (Attachment a : rec.getRecipientAtthFile()) {
//             //         logger.info("-- recipient attachment file size: {}",
//             //                 a.getFileContent().length);
//             //     }
//             // }
//             // Create and call the Notification send service

//     //         Response resp;
//     //         if ("Y".equals(includeAttachmentToMail)) {
//     //             // TODO: Temporary make the attachment array to null to avoid sending any too big attachment that lead to email malfunction
//     //             resp = notiRestfulClient.sendNotificationRequest(chanType,
//     //                     charSet, contentType, subject, content.getBytes(),
//     //                     attachments.toArray(new Attachment[attachments.size()]),
//     //                     recipientDetailArray, isEncrypt, isSign, isRestricted,
//     //                     isUrgent);
//     //         } else {
//     //             resp = notiRestfulClient.sendNotificationRequest(chanType,
//     //                     charSet, contentType, subject, content.getBytes(), null,
//     //                     recipientDetailArray, isEncrypt, isSign, isRestricted,
//     //                     isUrgent);
//     //         }

//     //         logger.info("[2]");
//     //         logger.info("resp status code : " + resp.getStatus());
//     //         logger.info("resp body: " + resp.readEntity(String.class));

//     //         if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
//     //             // String ResultCd = notiRestfulClient.getNotificationStatus(resp);
//     //             // System.out.println(ResultCd);
//     //             List<NotiStatus> notiStatusList = notiRestfulClient
//     //                     .getNotificationNotificationStatus(resp);
//     //             for (NotiStatus notiStatus : notiStatusList) {
//     //                 logger.warn("ChanAddr: " + notiStatus.getChanAddr());
//     //                 logger.warn("ResultCd: " + notiStatus.getResultCd());
//     //                 logger.warn("ResultMsg: " + notiStatus.getResultMesg());
//     //             }
//     //         } else {
//     //             ScopesFault scopesFault =
//     //                     notiRestfulClient.getScopesFault(resp);
//     //             logger.error("[Fail] {}", scopesFault.getDescription());
//     //             // handle the soap fault
//     //         }
//     //     } catch (Exception e) {
//     //         logger.error("[Exception Error]", e);
//     //     }
//     // }

//     // private boolean whitelistFilter(String to) {
//     //     AntPathMatcher pathMatcher = new AntPathMatcher();
//     //     if (whitelist != null) {
//     //         return Arrays.stream(whitelist)
//     //                 .anyMatch(p -> pathMatcher.match(p, to));
//     //     } else {
//     //         return false;
//     //     }
//     }

//     private Properties getSSLProperties(String endPointName, String endPointUrl)
//             throws Exception {
//         // String curPath = new File(".").getCanonicalPath();
//         //
//         // String password = "P@ssw0rd";
//         // String alias = "dsign";
//         // String propertyFile = curPath + "/proximity.properties";
//         // String keyStoreFile = curPath + "/ghbobsuat.csb.hksarg.p12";

//         Properties propAuth = new Properties();
//         propAuth.setProperty(PropertyNames.AUTH_TYPE_PROPERTY,
//                 PropertyNames.AUTH_SSL_CLIENT_X509_CERTIFICATE);
//         propAuth.setProperty(PropertyNames.PROXIMITY_CONFIG_FILE_PROPERTY,
//                 propertyFilePath);

//         propAuth.setProperty(PropertyNames.KEY_STORE_FILE_NAME_PROPERTY,
//                 keyStoreFilePath);
//         propAuth.setProperty(PropertyNames.KEY_STORE_PASSWORD_PROPERTY,
//                 keyStoreFilePassword);
//         propAuth.setProperty(PropertyNames.KEY_STORE_TYPE_PROPERTY,
//                 PropertyNames.KEY_STORE_TYPE_PKCS12);
//         propAuth.setProperty(PropertyNames.KEY_ALIAS_PROPERTY,
//                 keyStoreFileAlias);
//         propAuth.setProperty(PropertyNames.KEY_ALIAS_PASSWORD_PROPERTY,
//                 keyStoreFilePassword);

//         propAuth.setProperty(endPointName, endPointUrl);

//         return propAuth;
//     }


}
