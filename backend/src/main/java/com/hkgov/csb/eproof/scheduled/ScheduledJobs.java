package com.hkgov.csb.eproof.scheduled;

import com.hkgov.csb.eproof.util.EProof.EProofConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobs {


    private final NormalEmailSendJob normalEmailSendJob;
    private final EProofConfigProperties eProofConfigProperties;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduledJobs(NormalEmailSendJob normalEmailSendJob, EProofConfigProperties eProofConfigProperties) {
        this.normalEmailSendJob = normalEmailSendJob;
        this.eProofConfigProperties = eProofConfigProperties;
    }

    @Scheduled(cron = "${cron-expression.normal-email-sending}")
    public void normalEmailSending() throws InterruptedException {
        logStartMessage("Cert letter notify");
        normalEmailSendJob.start();
        logEndMessage("Cert letter notify");
    }

    @Scheduled(cron = "${cron-expression.refresh-eproof-access-token}")
    public void refreshCachedAccessToken()  {
        logStartMessage("Refresh cached access token");
        eProofConfigProperties.setAccessToken(null);
        logEndMessage("Refresh cached access token");
    }


    private void logStartMessage(String jobName){
        logger.info(String.format("[%s] Cronjob START",jobName));
    }
    private void logEndMessage(String jobName){
        logger.info(String.format("[%s] Cronjob END",jobName));
    }
}
