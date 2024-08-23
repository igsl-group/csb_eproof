package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.CertInfoRenewRepository;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.ExamProfileRepository;
import com.hkgov.csb.eproof.dto.ExamProfileCreateDto;
import com.hkgov.csb.eproof.dto.ExamProfileSummaryDto;
import com.hkgov.csb.eproof.dto.ExamProfileUpdateDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.ExamProfileMapper;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.ExamProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.SERIAL_HAS_EXITED;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.NOT_ALLOW_TO_RESET_EXAM_PROFILE;

@RequiredArgsConstructor
@Service
public class ExamProfileServiceImpl implements ExamProfileService {

    private final ExamProfileRepository examProfileRepository;

    private final CertInfoRepository certInfoRepository;
    private final CertInfoRenewRepository certInfoRenewRepository;
    private final CertInfoService certInfoService;

    @Override
    public Boolean create(ExamProfileCreateDto request) {
        var examProfile = examProfileRepository.getinfoByNo(request.getSerialNo());
        if(Objects.nonNull(examProfile)){
            throw new GenericException("400",SERIAL_HAS_EXITED);
        }
        ExamProfile exam = ExamProfileMapper.INSTANCE.destinationToSource(request);
        exam.setIsFreezed(false);
        exam.setStatus(Constants.STATUS_ACTIVE);
        exam = examProfileRepository.save(exam);
        return Objects.nonNull(exam);
    }

    @Override
    public void freeze(String examProfileSerialNo) {
        ExamProfile examProfile = examProfileRepository.findById(examProfileSerialNo).orElse(null);
        if(Objects.isNull(examProfile)){
            throw new GenericException(ExceptionEnums.EXAM_PROFILE_NOT_EXIST);
        }
        examProfile.setIsFreezed(true);
       examProfileRepository.save(examProfile);
    }

    @Override
    public void unfreeze(String examProfileSerialNo) {
        ExamProfile examProfile = examProfileRepository.findById(examProfileSerialNo).orElse(null);
        if(Objects.isNull(examProfile)){
            throw new GenericException(ExceptionEnums.EXAM_PROFILE_NOT_EXIST);
        }
        examProfile.setIsFreezed(false);
        examProfileRepository.save(examProfile);
    }

    @Override
    public void update(String id, ExamProfileUpdateDto request) {
        ExamProfile examProfile = examProfileRepository.findById(id).orElse(null);
        examProfile.setExamDate(request.getExamDate());
        examProfile.setPlannedEmailIssuanceDate(request.getPlannedEmailIssuanceDate());
        examProfile.setResultLetterDate(request.getResultLetterDate());
        examProfile.setLocation(request.getLocation());
        examProfile.setEffectiveDate(request.getEffectiveDate());
        examProfileRepository.save(examProfile);
    }
    @Override
    public ExamProfile getexamProfileInfo(String examProfileSerialNo) {
        return examProfileRepository.getinfoByNo(examProfileSerialNo);
    }

    @Override
    public Page<ExamProfile> list(Pageable pageable, String keyWord) {
        var examProfile = examProfileRepository.findPage(pageable,keyWord);
        return examProfile;
    }

    @Override
    public List<ExamProfile> dropDown() {
        return examProfileRepository.dropDown();
    }

    @Override
    public Boolean delete(String examProfileSerialNo) {
        List<CertInfo> certInfo = certInfoRepository.getInfoByExamProfileSerialNo(examProfileSerialNo);
        if(Objects.nonNull(certInfo) && certInfo.size()>0){
            throw new GenericException("400",SERIAL_HAS_EXITED);
        }
        return examProfileRepository.delExamProfile(examProfileSerialNo) > 0;
    }

    @Override
    public ExamProfileSummaryDto getSummary(String examProfileSerialNo) {
        return ExamProfileSummaryDto.builder()
                .imported(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.IMPORTED, null))

                .generatePdfFailed(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.GENERATED, CertStatus.FAILED))
                .generatePdfSuccess(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.GENERATED, CertStatus.SUCCESS))
                .generatePdfInProgress(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.GENERATED, CertStatus.IN_PROGRESS))
                .generatePdfTotal(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.GENERATED, null))

                .issuedPdfFailed(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.SIGN_ISSUE, CertStatus.FAILED))
                .issuedPdfSuccess(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.SIGN_ISSUE, CertStatus.SUCCESS))
                .issuedPdfInProgress(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.SIGN_ISSUE, CertStatus.IN_PROGRESS))
                .issuedPdfTotal(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.SIGN_ISSUE, null))

                .sendEmailFailed(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.NOTIFY, CertStatus.FAILED))
                .sendEmailSuccess(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.NOTIFY, CertStatus.SUCCESS))
                .sendEmailProgress(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.NOTIFY, CertStatus.IN_PROGRESS))
                .sendEmailTotal(certInfoRepository.countByStageAndStatus(examProfileSerialNo, CertStage.NOTIFY, null))

                .build();
    }

    @Override
    @Transactional
    public void reset(String examProfileSerialNo) throws Exception {

        //TODO Block reset if email already sent or complete stage
        List<CertInfo> certWithNotifyAndCompletedStageList = certInfoRepository.getInfoWithNotifyAndCompletedStageList(examProfileSerialNo);
        if (certWithNotifyAndCompletedStageList.size() > 0) {
            throw new GenericException("400",NOT_ALLOW_TO_RESET_EXAM_PROFILE);
        }

        List<CertInfo> certInfoList = certInfoRepository.getInfoListByExamSerialNo(examProfileSerialNo);

        //TODO Add revoke signed certificate
        for (CertInfo certInfo : certInfoList) {
            if (certInfo.getCertEproof() != null) {
                // Eproof record found
                certInfoService.actualRevokeWithEproofModule(certInfo.getId());
            }
        }
        if(Objects.nonNull(certInfoList) && !certInfoList.isEmpty()){
            certInfoRepository.deleteAll(certInfoList);
        }
    }
}
