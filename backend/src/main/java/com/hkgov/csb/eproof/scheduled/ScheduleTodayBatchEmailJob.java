package com.hkgov.csb.eproof.scheduled;

import com.hkgov.csb.eproof.dao.GcisBatchEmailRepository;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import com.hkgov.csb.eproof.service.GcisBatchEmailService;
import hk.gov.spica_scopes.spica.jaxb.schedule.ScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public class ScheduleTodayBatchEmailJob {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GcisBatchEmailRepository gcisBatchEmailRepository;
    private final GcisBatchEmailService gcisBatchEmailService;

    public ScheduleTodayBatchEmailJob(GcisBatchEmailRepository gcisBatchEmailRepository, GcisBatchEmailService gcisBatchEmailService) {
        this.gcisBatchEmailRepository = gcisBatchEmailRepository;
        this.gcisBatchEmailService = gcisBatchEmailService;
    }

    @Async("scheduledTaskThreadPool")
    public void start() throws Exception {

        List<GcisBatchEmail> todayPendingEmailEventList = gcisBatchEmailRepository.findPendingScheduleBatchEmailByDate(LocalDate.now());
        if (todayPendingEmailEventList != null){
            for (GcisBatchEmail gcisBatchEmail : todayPendingEmailEventList) {
                logger.info("Processing GCIS batch email id: " + gcisBatchEmail.getId());
                ScheduleResponse scheduleResponse = gcisBatchEmailService.scheduleBatchEmail(gcisBatchEmail,null);
                logger.info("scheduleResponse");
                logger.info(scheduleResponse.getResultCd());
                logger.info(scheduleResponse.getJobId());
                logger.info(scheduleResponse.getResultMesg());
                List<String> successRespCodeList = List.of("0000", "0077","0078","0079","0080");
                Boolean scheduleSuccess = scheduleResponse != null && successRespCodeList.contains(scheduleResponse.getResultCd());
                if(scheduleSuccess){
                    logger.info("GCIS batch email id: " + gcisBatchEmail.getId() + " scheduled successfully");
                    gcisBatchEmail.setScheduleJobId(scheduleResponse.getJobId());
                    gcisBatchEmailRepository.save(gcisBatchEmail);
                } else {
                    logger.error("GCIS batch email id: " + gcisBatchEmail.getId() + " schedule failed");
                }
            }
        }

    }

}

