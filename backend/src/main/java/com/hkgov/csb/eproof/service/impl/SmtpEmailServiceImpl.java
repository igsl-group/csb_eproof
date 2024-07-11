package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmtpEmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${mail.whitelist.enabled:false}")
    private boolean whitelistEnabled;
    @Value("${mail.whitelist.email-list}")
    private List<String> whitelist;
    @Value("${spring.mail.from}")
    private String mailFrom;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    public SmtpEmailServiceImpl(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }


    @Override
    public void sendEmail(List<String> to,
                          List<String> cc,
                          List<String> bcc,
                          String subject,
                          String content,
                          String attachmentName,
                          byte[] attachment) throws MessagingException {
        logger.info("Sending email. To: {},CC: {}, BCC: {}, Subject: {}",to,cc,bcc,subject);

        if(whitelistEnabled){
            to.removeIf(email -> !whitelist.contains(email));
            if(cc != null && !cc.isEmpty()){
                cc.removeIf(email -> !whitelist.contains(email));
            }
            if(bcc != null && !bcc.isEmpty()){
                bcc.removeIf(email -> !whitelist.contains(email));
            }
        }

        boolean hasAttachment = attachment != null && attachment.length > 0;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,hasAttachment,"UTF-8");

        helper.setFrom(mailFrom);
        helper.setTo(to.toArray(new String[0]));
        if(cc != null && !cc.isEmpty()){
            helper.setCc(cc.toArray(new String[0]));
        }
        if(bcc != null && !bcc.isEmpty()){
            helper.setBcc(bcc.toArray(new String[0]));
        }

        helper.setSubject(subject);
        helper.setText(content,true);


        if (hasAttachment){
            helper.addAttachment(attachmentName,new ByteArrayDataSource(attachment,"application/octet-stream"));
        }

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendBatchEmail(List<String> toList, String subject, String content, String attachmentName, byte[] attachment) throws MessagingException {
        if(toList != null && !toList.isEmpty()){
            for (String to : toList) {
                sendEmail(List.of(to),null,null,subject,content,attachmentName,attachment);
            }
        }
    }
}
