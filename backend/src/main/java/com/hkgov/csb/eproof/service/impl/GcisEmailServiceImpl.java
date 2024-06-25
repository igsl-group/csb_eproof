package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GcisEmailServiceImpl implements EmailService {

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
}
