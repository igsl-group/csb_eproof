package com.hkgov.csb.eproof.scheduled;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.GcisBatchEmailRepository;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.service.GcisBatchEmailService;
import hk.gov.spica_scopes.spica.jaxb.scheenq.ScheduleEnquiryResponse;
import hk.gov.spica_scopes.spica.notification.client.restful.NotificationRestfulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;


@Component
public class EnquireAndUpdateTodayScheduleStatusJob {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GcisBatchEmailRepository gcisBatchEmailRepository;
    private final CertInfoRepository certInfoRepository;
    private final GcisBatchEmailService gcisBatchEmailService;
    @Value("${gcis-shared-service.notiSvc.batchUploadEndPointName}")
    String batchUploadEndPointName;

    @Value("${gcis-shared-service.notiSvc.batchUploadEndPointUrl}")
    String batchUploadEndPointUrl;

    public EnquireAndUpdateTodayScheduleStatusJob(GcisBatchEmailRepository gcisBatchEmailRepository, CertInfoRepository certInfoRepository, GcisBatchEmailService gcisBatchEmailService) {
        this.gcisBatchEmailRepository = gcisBatchEmailRepository;
        this.certInfoRepository = certInfoRepository;
        this.gcisBatchEmailService = gcisBatchEmailService;
    }

    @Async("scheduledTaskThreadPool")
    public void start() throws Exception {
        Properties prop = gcisBatchEmailService.getSSLProperties(batchUploadEndPointName, batchUploadEndPointUrl);
        NotificationRestfulClient notiRestfulClient = new NotificationRestfulClient(prop);

        List<GcisBatchEmail> todayPendingEnquireEmail = gcisBatchEmailRepository.findPendingEnquireBatchEmailByDate(LocalDate.now());
        if (todayPendingEnquireEmail != null){
            for (GcisBatchEmail gcisBatchEmail : todayPendingEnquireEmail) {
                logger.info("Enquiring GCIS batch email id: " + gcisBatchEmail.getId());
                Response enquireResp = gcisBatchEmailService.enquireScheduleStatus(gcisBatchEmail.getId());
                if(enquireResp.getStatus() == Response.Status.OK.getStatusCode()){

                    ScheduleEnquiryResponse ser = notiRestfulClient.getScheduleEnquiryResponse(enquireResp);
                    // 0080 = COMPLETED
                    gcisBatchEmail.setScheduleJobStatus(ser.getResultMesg());
                    gcisBatchEmailRepository.save(gcisBatchEmail);
                    List<String> successRespCodeList = List.of("0080");
                    Boolean scheduleSuccess = ser != null && successRespCodeList.contains(ser.getResultCd());
                    if (scheduleSuccess) {
                        LocalDateTime scheduleEstEndTime = LocalDateTime.parse(gcisBatchEmail.getScheduleEstEndTime(), DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_3));
                        certInfoRepository.updateNotifyStatusByGcisBatchEmailId(gcisBatchEmail.getId(), scheduleEstEndTime);
                    }

                    logger.info("GCIS batch email id: " + gcisBatchEmail.getId() + " enquire successfully");
                } else {
                    gcisBatchEmail.setScheduleJobStatus("ENQUIRE_FAILED");
                    gcisBatchEmail.setScheduleJobRemark(notiRestfulClient.getScopesFault(enquireResp).getDescription());
                    gcisBatchEmailRepository.save(gcisBatchEmail);
                    logger.error("GCIS batch email id: " + gcisBatchEmail.getId() + " enquire failed");
                }
            }
        }
    }

}

