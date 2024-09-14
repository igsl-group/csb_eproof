package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.GcisBatchEmail;

import java.time.LocalDateTime;

public interface GcisBatchEmailService {
    Boolean uploadBatchXmlToGcis(Long gcisBatchEmailId) throws Exception;
    Boolean uploadBatchXmlToGcis(GcisBatchEmail gcisBatchEmail) throws Exception;

    String scheduleBatchEmail(Long gcisBatchEmailId, LocalDateTime scheduleTime) throws Exception;

    String enquireUploadStatus(Long gcisBatchEmailId) throws Exception;
    String enquireScheduleStatus(Long gcisBatchEmailId) throws Exception;

    String deleteBatchEmailListFromGcis(Long gcisBatchEmailId) throws Exception;
}
