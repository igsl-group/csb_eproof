package com.hkgov.csb.eproof.scheduled;

import com.hkgov.csb.eproof.dao.GcisBatchEmailRepository;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import com.hkgov.csb.eproof.service.GcisBatchEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public class UploadTodayBatchXmlJob {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GcisBatchEmailRepository gcisBatchEmailRepository;
    private final GcisBatchEmailService gcisBatchEmailService;

    public UploadTodayBatchXmlJob(GcisBatchEmailRepository gcisBatchEmailRepository, GcisBatchEmailService gcisBatchEmailService) {
        this.gcisBatchEmailRepository = gcisBatchEmailRepository;
        this.gcisBatchEmailService = gcisBatchEmailService;
    }

    @Async("scheduledTaskThreadPool")
    public void start() throws Exception {

        List<GcisBatchEmail> todayPendingEmailEventList = gcisBatchEmailRepository.findPendingUploadBatchEmailByDate(LocalDate.now());

        for (GcisBatchEmail gcisBatchEmail : todayPendingEmailEventList) {
            logger.info("Processing GCIS batch email id: " + gcisBatchEmail.getId());
            Boolean uploadSuccess = true;
            try{
                uploadSuccess = gcisBatchEmailService.uploadBatchXmlToGcis(gcisBatchEmail);
            } catch(Exception e){
                logger.error("encountered error while uploading: ",e);
                uploadSuccess = false;
            }
            if(uploadSuccess){
                logger.info("GCIS batch email id: " + gcisBatchEmail.getId() + " uploaded successfully");
            } else {
                logger.error("GCIS batch email id: " + gcisBatchEmail.getId() + " upload failed");
            }
        }

        logger.info("All GCIS batch email uploaded successfully");
    }

}

