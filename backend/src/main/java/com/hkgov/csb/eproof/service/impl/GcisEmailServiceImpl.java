package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.dao.EmailMessageRepository;
import com.hkgov.csb.eproof.dao.ExamProfileRepository;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.entity.EmailMessage;
import com.hkgov.csb.eproof.event.EmailEventPublisher;
import com.hkgov.csb.eproof.request.SendEmailRequest;
import com.hkgov.csb.eproof.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class GcisEmailServiceImpl implements EmailService {

    @Autowired
    EmailEventRepository emailEventRepository;
    @Autowired
    EmailMessageRepository emailMessageRepository;
    @Autowired
    EmailEventPublisher emailEventPublisher;

    @Override
    public void sendEmail(List<String> to,
                          List<String> cc,
                          List<String> bcc,
                          String subject,
                          String content,
                          String attachmentName,
                          byte[] attachment) throws MessagingException {
       /* boolean hasAttachment = attachment != null && attachment.length > 0;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,hasAttachment,"UTF-8");

        helper.setTo(to.toArray(new String[0]));
        helper.setCc(cc.toArray(new String[0]));
        helper.setBcc(bcc.toArray(new String[0]));

        helper.setSubject(subject);
        helper.setText(content,true);

        if (hasAttachment){
            helper.addAttachment(attachmentName,new ByteArrayDataSource(attachment,"application/octet-stream"));
        }*/


    }

    @Override
    public void sendBatchEmail(List<String> to, String subject, String content, String attachmentName, byte[] attachment) throws MessagingException {

    }

    public Boolean sendTestEmail(String to, String title, String htmlBody) {
        String[] emailAddresses = to.split(",");
        for (String email : emailAddresses) {
            email = email.trim();
            EmailEvent event = this.createCustomEmailEvent(email, title, htmlBody);
            emailEventPublisher.publicEmailEvent(event);
        }
        return true;
    }

    @Transactional
    public EmailEvent createCustomEmailEvent(String emailTo, String customTitle, String htmlBody) {
        return emailEventRepository.save(this.prepareCustomEmailEvent(emailTo, customTitle, htmlBody));
    }

    public EmailEvent prepareCustomEmailEvent(String emailTo, String customTitle, String customHtmlBody) {
        EmailEvent event = new EmailEvent();
        EmailMessage message = new EmailMessage();
        message.setTo(emailTo);
        message.setSubject(customTitle);
        message.setBody(customHtmlBody);
        emailMessageRepository.save(message);

        event.setEmailMessage(message);

        return event;
    }

}
