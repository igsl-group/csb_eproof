package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.dao.*;
import com.hkgov.csb.eproof.dto.CertDetailDto;
import com.hkgov.csb.eproof.dto.CertRenewSearchDto;
import com.hkgov.csb.eproof.dto.CertRevokeDto;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertActionMapper;
import com.hkgov.csb.eproof.service.CertInfoRenewService;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.LetterTemplateService;
import com.hkgov.csb.eproof.util.HttpUtils;
import com.hkgov.csb.eproof.util.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.hkgov.csb.eproof.constants.Constants.LETTER_TEMPLATE_ALL_FAILED_TEMPLATE;
import static com.hkgov.csb.eproof.constants.Constants.LETTER_TEMPLATE_AT_LEAST_ONE_PASS;

/**
* @author 20768
* @description 针对表【cert_info_renew】的数据库操作Service实现
* @createDate 2024-06-05 17:19:02
*/
@Service
@RequiredArgsConstructor
public class CertInfoRenewServiceImpl implements CertInfoRenewService {
    private final CertInfoRenewRepository certInfoRenewRepository;
    private final LetterTemplateService letterTemplateService;
    private final CertInfoService certInfoService;
    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;
    private final UserRepository userRepository;
    private final CertActionRepository certActionRepository;
    private final ActionTargetRepository actionTargetRepository;
    @Override
    public void changeCertStatusToInProgress(Long certInfoId, CertStage certStage) {

    }

    @Override
    public void batchGeneratePdf(Long certInfoId) throws Exception {
        List<CertInfoRenew> inProgressCertList = certInfoRenewRepository.findAllById(List.of(certInfoId))/*.stream().filter(
                x -> x.getCertStage().equals(CertStage.GENERATED) && CertStatus.IN_PROGRESS.getCode().equals(x.getStatus())).collect(Collectors.toList())*/;
        byte[] passTemplateInputStream = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_AT_LEAST_ONE_PASS);
        byte[] allFailedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE);
        for (CertInfoRenew info : inProgressCertList) {
            CertInfo certInfo = new CertInfo();
            certInfo.setEmail(info.getNewEmail());
            certInfo.setHkid(info.getNewHkid());
            certInfo.setId(info.getId());
            certInfo.setLetterType(info.getLetterType());
            certInfo.setName(info.getNewName());
//            certInfo.setCname(info.getNewCname());
            certInfo.setPassportNo(info.getNewPassport());
            certInfo.setAtGrade(info.getNewAtGrade());
            certInfo.setBlnstGrade(info.getNewBlGrade());
            certInfo.setUcGrade(info.getNewUcGrade());
            certInfo.setUeGrade(info.getNewUeGrade());
            certInfo.setRemark(info.getRemark());
            certInfo.setCertStage(info.getCertStage());
            certInfo.setCertStatus(info.getStatus());
            certInfoService.singleGeneratePdf(certInfo,passTemplateInputStream,allFailedTemplate,true,true);
        }
    }

    @Override
    public void removeCert(Long certInfoId) {
        certInfoRenewRepository.updateIsDeleteById(certInfoId);
    }

    @Override
    public byte[] getZippedPdfBinary(List<Long> certInfoIdList) throws IOException {
        List<CertInfoRenew> certInfoList = certInfoRenewRepository.findAllById(certInfoIdList);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (CertInfoRenew info : certInfoList) {
            if(info.getPdfList() == null || info.getPdfList().size()<=0){
                continue;
            }
            File latestPdf = fileRepository. getLatestPdfForCertRenewId(info.getId());
            ZipEntry zipEntry = new ZipEntry(latestPdf.getName());
            zos.putNextEntry(zipEntry);
            zos.write(minioUtil.getFileAsByteArray(latestPdf.getPath()));
            zos.closeEntry();
        }
        zos.finish();
        zos.close();
        return baos.toByteArray();
    }

    @Override
    public void revoke(List<Long> certInfoIdList, CertRevokeDto params) {
        String userName = HttpUtils.getUser();
        User user = userRepository.getUserByDpUserId(userName);
        CertAction certAction = new CertAction();
        certAction.setApprover(user.getId());
        certAction.setRemark(params.getRemark());
        certAction.setCanEmailAddress(params.getEmailTarget());
        certAction.setCanEmailContent(params.getEmailContent());
        certAction.setType("REVOKE");
        certAction.setStatus(CertStatus.PENDING);
        certAction = certActionRepository.save(certAction);
        List<ActionTarget> targets = new ArrayList<>();
        for (Long certInfoId : certInfoIdList) {
            ActionTarget target = new ActionTarget();
            target.setActionId(certAction.getId());
            target.setTargetCertInfoId(certInfoId);
            targets.add(target);
        }
        actionTargetRepository.saveAll(targets);
    }

    @Override
    public CertRevokeDto getTodoRevoke() {
        String userName = HttpUtils.getUser();
        User user = userRepository.getUserByDpUserId(userName);
        List<CertAction> certActions = certActionRepository.findByUser(user.getId());
        CertRevokeDto certRevokeDto = new CertRevokeDto();
        List<CertDetailDto> certInfos = new ArrayList<>();
        for(int i = 0; i < certActions.size(); i++){
            CertAction certAction = certActions.get(i);
            if(i == 0){
                certRevokeDto.setName(certAction.getCertInfos().get(0).getName());
                certRevokeDto.setHkid(certAction.getCertInfos().get(0).getHkid());
            }
            CertRevokeDto revokeDto = CertActionMapper.INSTANCE.sourceToDestination(certAction);
            certInfos.addAll(revokeDto.getCertInfos());
        }
        certRevokeDto.setCertInfos(certInfos);
        return certRevokeDto;
    }

    @Override
    public Page<CertInfoRenew> search(CertRenewSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable) {
        return certInfoRenewRepository.certSearch(request,certStageList,certStatusList,pageable);
    }

    @Override
    public void dispatch(Long id, CertStage currentStage) {
        if(!currentStage.equals(CertStage.RENEWED) && !currentStage.equals(CertStage.GENERATED)
                && !currentStage.equals(CertStage.SIGN_ISSUE) && !currentStage.equals(CertStage.NOTIFY)){
            throw new ServiceException(ResultCode.STAGE_ERROR);
        }
        List<CertInfoRenew> list = certInfoRenewRepository.getinfoByNoAndStatus(id,currentStage);
        if(list.isEmpty()){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        for(CertInfoRenew infoRenew : list){
            switch (infoRenew.getCertStage()) {
                case RENEWED -> {
                    infoRenew.setCertStage(CertStage.GENERATED);
                    break;
                }
                case GENERATED -> {
                    infoRenew.setCertStage(CertStage.SIGN_ISSUE);
                    break;
                }
                case SIGN_ISSUE -> {
                    infoRenew.setCertStage(CertStage.NOTIFY);
                    break;
                }
                case NOTIFY -> {
                    infoRenew.setCertStage(CertStage.COMPLETED);
                    break;
                }
                default ->{
                    break;
                }
            }
            infoRenew.setStatus(CertStatus.PENDING);
        }
        certInfoRenewRepository.saveAll(list);
    }
}
