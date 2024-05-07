package com.hkgov.csb.eproof.event;

import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class EmailEventListener {
    private static final Logger logger = LoggerFactory.getLogger(EmailEventListener.class);
    private final EmailService emailService;
    private final EmailEventRepository emailEventRepository;

    public EmailEventListener(EmailService emailService, EmailEventRepository emailEventRepository) {
        this.emailService = emailService;
        this.emailEventRepository = emailEventRepository;
    }

    @Async
    @EventListener
    public void handleAsyncEmailEvent(EmailEvent event) {
        try {
            emailService.sendEmail(event);
            event.setStatus("SUCCESS");
        } catch (Exception e) {
            logger.error("handleAsyncEmailEvent Failed Pk:" + event.getEmailEventId() + " Reason:" + e.getMessage());
            event.setStatus("FAILED");
        }
        emailEventRepository.save(event);
    }
}
