package com.hkgov.csb.eproof.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.dao.*;
import com.hkgov.csb.eproof.dto.CertInfoDto;
import com.hkgov.csb.eproof.dto.CertRenewSearchDto;
import com.hkgov.csb.eproof.dto.CertRevokeDto;
import com.hkgov.csb.eproof.dto.ExamScoreDto;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertActionMapper;
import com.hkgov.csb.eproof.service.*;
import com.hkgov.csb.eproof.util.DocxUtil;
import com.hkgov.csb.eproof.util.HttpUtils;
import com.hkgov.csb.eproof.util.MinioUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.docx4j.model.fields.merge.DataFieldName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.hkgov.csb.eproof.constants.Constants.*;

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
    private final CertRenewPdfRepository certRenewPdfRepository;
    private final DocumentGenerateService documentGenerateService;
    private final DocxUtil docxUtil;
    private final FileService fileService;

    @Value("${minio.path.cert-renew-record}")
    private String certRenewRecordPath;

    @Override
    public void changeCertStatusToInProgress(Long certInfoId, CertStage certStage) {

    }


    private Map<DataFieldName, String> getMergeMapForRenewCert(CertInfoRenew certInfoRenew) throws JsonProcessingException {
        ExamProfile exam = certInfoRenew.getCertInfo().getExamProfile();

        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfoRenew,"cert");

        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        // Change the format of date for examMap
        examMap.put("examProfile.examDate",exam.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_2)));
        examMap.put("examProfile.resultLetterDate",exam.getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_2)));

        return docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap);
    }



    private Map<String,List> getTableLoopMapForCertRenew(CertInfoRenew certInfoRenew){
        List<ExamScoreDto> markDtoList = new ArrayList<>();

        if(StringUtils.isNotEmpty(certInfoRenew.getNewUcGrade())){
            markDtoList.add(new ExamScoreDto("Use of Chinese",convertGradeToReadableGrade(certInfoRenew.getNewUcGrade())));
        }
        if(StringUtils.isNotEmpty(certInfoRenew.getNewUeGrade())){
            markDtoList.add(new ExamScoreDto("Use of English",convertGradeToReadableGrade(certInfoRenew.getNewUeGrade())));
        }
        if(StringUtils.isNotEmpty(certInfoRenew.getNewAtGrade())){
            markDtoList.add(new ExamScoreDto("Aptitude Test",convertGradeToReadableGrade(certInfoRenew.getNewAtGrade())));
        }
        if(StringUtils.isNotEmpty(certInfoRenew.getNewBlGrade())) {
            markDtoList.add(new ExamScoreDto("Basic Law and National Security Law Test", convertGradeToReadableGrade(certInfoRenew.getNewBlGrade())));
        }

        HashMap<String,List> map = new HashMap<>();
        map.put("examResults",markDtoList);
        return map;
    }

    private String convertGradeToReadableGrade(String originalGrade){
        if (originalGrade == null){
            return "";
        }
        String returnString = "";
        switch(originalGrade){
            case "P": returnString = "Pass"; break;
            case "F": returnString = "Fail"; break;
            case "L1": returnString = "Level 1"; break;
            case "L2": returnString = "Level 2"; break;
        }

        return returnString;
    }

    @Override
    public void singleGeneratePdf(Long renewCertInfoId) throws Exception {
        CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(renewCertInfoId).get();
        try{
            byte[] atLeastOnePassedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_AT_LEAST_ONE_PASS);
            byte[] allFailedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE);
            InputStream appliedTemplate = "P".equals(certInfoRenew.getNewLetterType())?new ByteArrayInputStream(atLeastOnePassedTemplate):new ByteArrayInputStream(allFailedTemplate);

            byte [] mergedPdf = documentGenerateService.getMergedDocument(appliedTemplate, DocumentOutputType.PDF,getMergeMapForRenewCert(certInfoRenew),getTableLoopMapForCertRenew(certInfoRenew));
            appliedTemplate.close();
            IOUtils.close(appliedTemplate);

            File uploadFileRecord = this.uploadCertPdf(certInfoRenew, mergedPdf);
            this.createCertRenewPdfRecord(certInfoRenew,uploadFileRecord);
            this.updateCertStageAndStatus(certInfoRenew,CertStage.GENERATED,CertStatus.SUCCESS);

        } catch (Exception e){
            certInfoRenew.setCertStatus(CertStatus.FAILED);
            e.printStackTrace();
            certInfoRenewRepository.save(certInfoRenew);
        }

       /* for (CertInfoRenew info : inProgressCertList) {
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
        }*/
    }

    private void updateCertStageAndStatus(CertInfoRenew certInfoRenew,CertStage stage, CertStatus status){
        if(stage != null){
            certInfoRenew.setCertStage(stage);
        }

        if(status != null){
            certInfoRenew.setCertStatus(status);
        }

        certInfoRenewRepository.save(certInfoRenew);
    }

    private File uploadCertPdf(CertInfoRenew certInfoRenew, byte[] mergedPdf) throws IOException {

        String processedCertOwnerName = certInfoRenew.getNewName().trim().replace(" ","_");
        String currentTimeMillisString = String.valueOf(System.currentTimeMillis());
        String savePdfName = String.format("%s_%s_%s.pdf",
                processedCertOwnerName,
                currentTimeMillisString,
                UUID.randomUUID().toString().replace("-","")
        );
        return fileService.uploadFile(FILE_TYPE_CERT_RECORD,certRenewRecordPath,savePdfName,new ByteArrayInputStream(mergedPdf));
    }


    public void createCertRenewPdfRecord(CertInfoRenew certInfoRenew,File uploadedPdf){
        CertRenewPdf certPdf = new CertRenewPdf();
        certPdf.setCertInfoRenewId(certInfoRenew.getId());
        certPdf.setFileId(uploadedPdf.getId());
        certRenewPdfRepository.save(certPdf);
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
        List<CertInfoDto> certInfos = new ArrayList<>();
        for(int i = 0; i < certActions.size(); i++){
            CertAction certAction = certActions.get(i);
            CertRevokeDto revokeDto = CertActionMapper.INSTANCE.sourceToDestination(certAction);
            certInfos.addAll(revokeDto.getCertInfos());
        }
        certRevokeDto.setCertInfos(certInfos);
        if(CollUtil.isNotEmpty(certInfos)){
            for(CertInfoDto infoDto : certInfos){
                certRevokeDto.setName(StringUtils.isBlank(certRevokeDto.getName()) ? infoDto.getName() : certRevokeDto.getName());
                certRevokeDto.setHkid(StringUtils.isBlank(certRevokeDto.getHkid()) ? infoDto.getHkid() : certRevokeDto.getHkid());
            }
        }
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
            infoRenew.setCertStatus(CertStatus.PENDING);
        }
        certInfoRenewRepository.saveAll(list);
    }
}
