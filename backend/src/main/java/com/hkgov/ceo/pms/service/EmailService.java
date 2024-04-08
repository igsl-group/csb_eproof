package com.hkgov.ceo.pms.service;


import com.hkgov.ceo.pms.entity.EmailContext;
import com.hkgov.ceo.pms.entity.EmailEvent;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(EmailEvent event) throws MessagingException;

    EmailEvent createEmailEvent(String to, String cc, String templateId, String context);


    String convertContextToJson(EmailContext context);
}
