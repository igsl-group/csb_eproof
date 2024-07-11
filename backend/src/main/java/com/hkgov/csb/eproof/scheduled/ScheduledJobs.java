package com.hkgov.csb.eproof.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobs {


    private final NormalEmailSendJob normalEmailSendJob;


    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduledJobs(NormalEmailSendJob normalEmailSendJob) {
        this.normalEmailSendJob = normalEmailSendJob;
    }

    @Scheduled(cron = "${cron-expression.normal-email-sending}")
    public void normalEmailSending() throws InterruptedException {
        logStartMessage("Cert letter notify");
        normalEmailSendJob.start();
        logEndMessage("Cert letter notify");
    }


    private void logStartMessage(String jobName){
        logger.info(String.format("[%s] Cronjob START",jobName));
    }
    private void logEndMessage(String jobName){
        logger.info(String.format("[%s] Cronjob END",jobName));
    }
}
