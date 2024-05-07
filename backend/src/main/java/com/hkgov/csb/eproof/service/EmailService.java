package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.entity.EmailContext;
import com.hkgov.csb.eproof.entity.EmailEvent;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(EmailEvent event) throws MessagingException;

    EmailEvent createEmailEvent(String to, String cc, String templateId, String context);


    String convertContextToJson(EmailContext context);
}
