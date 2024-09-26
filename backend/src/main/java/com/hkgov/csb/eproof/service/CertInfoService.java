package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.*;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import freemarker.template.TemplateException;
import org.dom4j.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service
* @createDate 2024-05-10 17:47:40
*/
public interface CertInfoService {

    Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable);

    Boolean batchImport(String examProfileSerialNo, List<CertImportDto> csvData);

    Boolean dispatch(String examProfileSerialNo, CertStage currentStage);


    void changeCertStatusToInProgress(String examProfileSerialNo, CertStage certStage);
    List<CertInfo> changeCertStatusToScheduled(String examProfileSerialNo, CertStage certStage);

    Boolean batchScheduleCertSignAndIssue(String examProfileSerialNo);

    void batchGeneratePdf(String examProfileSerialNo) throws Exception;

    void singleGeneratePdf(CertInfo certInfo,
                           byte[] atLeastOnePassedTemplate,
                           byte [] allFailedTemplate,
                           boolean isBatchMode,boolean isNewCertInfo) throws Exception;
    byte [] getZippedPdfBinary(List<Long> certInfoIdList) throws IOException;

    byte [] previewCertPdf(Long certInfoId) throws Exception;

    Boolean updateEmail(UpdateEmailDto updateEmailDto);

    Boolean updatePersonalParticular(UpdatePersonalDto personalDto);

    void updatePersonalParticularById(Long certInfoId,UpdatePersonalDto personalDto);

    Boolean updateResult(Long certInfoId, UpdateResultDto resultDto);

    void batchSignAndIssue(String examProfileSerialNo);

    void resume(Long certInfoId, String remark);

    void hold(Long certInfoId, String remark);

    void delete(Long certInfoId);

    CertInfo getNextScheduledSignAndIssueCert(String examProfileSerialNo);

    void notifyInternalUserSignAndIssueCompleted(String examProfileSerialNo) throws TemplateException, IOException;

    void uploadSignedPdf(Long certInfoId, MultipartFile file);

    void issueCert(Long certInfoId) throws Exception;

    String prepareEproofUnsignJson(Long certInfoId);

    byte[] prepareEproofPdf(Long certInfoId, PrepareEproofPdfRequest prepareEproofPdfRequest) throws Exception;

    void insertGcisBatchEmail(String examProfileSerialNo, InsertGcisBatchEmailDto insertGcisBatchEmailDto) throws DocumentException, IOException, TemplateException;

    void approveRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception;

    void rejectRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception;

    void resubmitRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception;

    void removeRevoke(Long certActionId) throws Exception;

    void actualRevokeWithEproofModule(Long certInfoId, String remark) throws Exception;

    ResponseEntity<byte[]> enquiryResult(List<String> params);

    ResponseEntity<List<CertInfo>> getRamdomPdf(String examProfileSerialNo, Integer allPassed, Integer partialFailed, Integer allFailed);

    void deleteFutureBatchEmail(String examProfileSerialNo);

    byte [] downloadcert(String examProfileId) throws IOException;
}
