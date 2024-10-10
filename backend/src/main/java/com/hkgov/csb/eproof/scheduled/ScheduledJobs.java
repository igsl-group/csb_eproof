package com.hkgov.csb.eproof.scheduled;

import com.hkgov.csb.eproof.util.EProof.ApiUtil;
import com.hkgov.csb.eproof.util.EProof.EProofConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobs {


    private final NormalEmailSendJob normalEmailSendJob;
    private final UploadTodayBatchXmlJob uploadTodayBatchXmlJob;
    private final ScheduleTodayBatchEmailJob scheduleTodayBatchEmailJob;
    private final EnquireAndUpdateTodayScheduleStatusJob enquireAndUpdateScheduleStatusJob;


    private final EProofConfigProperties eProofConfigProperties;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduledJobs(NormalEmailSendJob normalEmailSendJob, UploadTodayBatchXmlJob uploadTodayBatchXmlJob, ScheduleTodayBatchEmailJob scheduleTodayBatchEmailJob, EnquireAndUpdateTodayScheduleStatusJob enquireAndUpdateScheduleStatusJob, EProofConfigProperties eProofConfigProperties) {
        this.normalEmailSendJob = normalEmailSendJob;
        this.uploadTodayBatchXmlJob = uploadTodayBatchXmlJob;
        this.scheduleTodayBatchEmailJob = scheduleTodayBatchEmailJob;
        this.enquireAndUpdateScheduleStatusJob = enquireAndUpdateScheduleStatusJob;
        this.eProofConfigProperties = eProofConfigProperties;
    }

    @Scheduled(cron = "${cron-expression.normal-email-sending}")
    public void normalEmailSending() throws InterruptedException {
        logStartMessage("Cert letter notify");
        normalEmailSendJob.start();
        logEndMessage("Cert letter notify");
    }

    @Scheduled(cron = "${cron-expression.refresh-eproof-access-token}")
    public void refreshCachedAccessToken() throws Exception {
        logStartMessage("Refresh cached access token");
        eProofConfigProperties.setAccessToken(null);
        ApiUtil.getAccessTokenByClientCredentials(eProofConfigProperties);
        logEndMessage("Refresh cached access token");
    }


    @Scheduled(cron = "${cron-expression.upload-today-batch-xml-to-gcis}")
    public void uploadTodayBatchToGcis() throws Exception {
        logStartMessage("Upload batch email xml");
        uploadTodayBatchXmlJob.start();
        logEndMessage("Upload batch email xml");
    }

    @Scheduled(cron = "${cron-expression.schedule-today-batch-email}")
    public void scheduleBatchEmail() throws Exception {
        logStartMessage("Schedule mail");
        scheduleTodayBatchEmailJob.start();
        logEndMessage("Schedule mail");
    }

    @Scheduled(cron = "${cron-expression.enquire-and-update-schedule-status}")
    public void enquireAndUpdateScheduleStatus() throws Exception{
        logStartMessage("Enquire and update schedule status");
        enquireAndUpdateScheduleStatusJob.start();
        logEndMessage("Enquire and update schedule status");
    }
    private void logStartMessage(String jobName){
        logger.info(String.format("[%s] Cronjob START",jobName));
    }
    private void logEndMessage(String jobName){
        logger.info(String.format("[%s] Cronjob END",jobName));
    }
}
