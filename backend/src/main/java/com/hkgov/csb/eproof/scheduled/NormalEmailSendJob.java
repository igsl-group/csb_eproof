package com.hkgov.csb.eproof.scheduled;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.dao.EmailEventRepository;
import com.hkgov.csb.eproof.entity.EmailEvent;
import com.hkgov.csb.eproof.entity.EmailMessage;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.service.EmailEventService;
import com.hkgov.csb.eproof.service.EmailService;
import com.hkgov.csb.eproof.util.MinioUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class NormalEmailSendJob {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmailEventRepository emailEventRepository;
    private final EmailEventService emailEventService;
    private final EmailService emailService;
    private final MinioUtil minioUtil;

    public NormalEmailSendJob(EmailEventRepository emailEventRepository, EmailEventService emailEventService, EmailService emailService, MinioUtil minioUtil) {
        this.emailEventRepository = emailEventRepository;
        this.emailEventService = emailEventService;
        this.emailService = emailService;
        this.minioUtil = minioUtil;
    }

    @Async("scheduledTaskThreadPool")
    public void start() throws InterruptedException {
        List<EmailEvent> pendingEmailEventList = emailEventRepository.findPendingEmailEvent();

        if(pendingEmailEventList == null){
            logger.info("No pending email event found");
            return;
        }
        emailEventService.updateEmailEventStatus(pendingEmailEventList, Constants.EMAIL_STATUS_IN_PROGRESS);

        for(EmailEvent emailEvent : pendingEmailEventList){
            try{
                EmailMessage emailMessage = emailEvent.getEmailMessage();
                File attachment = emailMessage.getAttachment();
                emailService.sendEmail(
                    Arrays.asList(emailMessage.getTo().split(",")),
                    emailMessage.getCc() != null? Arrays.asList(emailMessage.getCc().split(",")) : null,
                    emailMessage.getBcc() != null? Arrays.asList(emailMessage.getBcc().split(",")) : null,
                    emailMessage.getSubject(),
                    emailMessage.getBody(),
                    attachment != null? attachment.getName() : null,
                    attachment != null? minioUtil.getFileAsByteArray(attachment.getPath()):null
                );
                emailEventService.updateEmailEventStatus(List.of(emailEvent), Constants.EMAIL_STATUS_COMPLETED);
            }catch (Exception e){
                e.printStackTrace();
                logger.error("Error sending email. Email event id: {}",emailEvent.getId());
                emailEventService.updateEmailEventStatus(List.of(emailEvent), Constants.EMAIL_STATUS_FAILED);
            }
        }
    }

}

