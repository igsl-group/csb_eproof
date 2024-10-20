package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import hk.gov.spica_scopes.spica.jaxb.batchenq.BatchUploadEnquiryResponse;
import hk.gov.spica_scopes.spica.jaxb.schedule.ScheduleResponse;
import hk.gov.spica_scopes.spica.jaxb.scheenq.ScheduleEnquiryResponse;
import hk.gov.spica_scopes.spica.jaxb.batchupload.BatchUploadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Properties;

public interface GcisBatchEmailService {
    Boolean uploadBatchXmlToGcis(Long gcisBatchEmailId) throws Exception;
    Boolean uploadBatchXmlToGcis(GcisBatchEmail gcisBatchEmail) throws Exception;

    ScheduleResponse scheduleBatchEmail(Long gcisBatchEmailId, LocalDateTime scheduleTime) throws Exception;

    ScheduleResponse createSchedule(String startTimestamp, String notiListName, String templateName) throws Exception;

    ScheduleResponse scheduleBatchEmail(GcisBatchEmail gcisBatchEmail, LocalDateTime scheduleTime) throws Exception;

    BatchUploadResponse batchUpload(MultipartFile file) throws Exception;

    BatchUploadEnquiryResponse enquireUploadStatus(Long gcisBatchEmailId) throws Exception;

    BatchUploadEnquiryResponse enquireUploadStatusByBatchUploadRefNum(String batchUploadRefNum) throws Exception;

    ScheduleEnquiryResponse enqSchedule(String jobId) throws Exception;

    Response enquireScheduleStatus(Long gcisBatchEmailId) throws Exception;

    String deleteBatchEmailListFromGcis(Long gcisBatchEmailId) throws Exception;

    Properties getSSLProperties(String endPointName, String endPointUrl)
            throws Exception;

    Page<GcisBatchEmail> batchEmailList(Pageable pageable, String keyword);
}
