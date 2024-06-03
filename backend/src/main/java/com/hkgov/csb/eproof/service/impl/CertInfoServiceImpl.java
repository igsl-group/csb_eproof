package com.hkgov.csb.eproof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.CertPdfRepository;
import com.hkgov.csb.eproof.dto.CertImportDto;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.dto.ExamScoreDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.CertPdf;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertInfoMapper;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.DocumentGenerateService;
import com.hkgov.csb.eproof.service.FileService;
import com.hkgov.csb.eproof.service.LetterTemplateService;
import com.hkgov.csb.eproof.util.CodeUtil;
import com.hkgov.csb.eproof.util.DocxUtil;
import com.hkgov.csb.eproof.util.MinioUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.units.qual.C;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.hkgov.csb.eproof.constants.Constants.*;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service实现
* @createDate 2024-05-10 17:47:40
*/
@Service
@RequiredArgsConstructor
public class CertInfoServiceImpl implements CertInfoService {

    @Value("${minio.path.cert-record}")
    private String certRecordPath;

    private final CertInfoRepository certInfoRepository;
    private final DocumentGenerateService documentGenerateService;
    private final LetterTemplateService letterTemplateService;
    private final DocxUtil docxUtil;
    private final FileService fileService;
    private final EntityManager entityManager;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CertPdfRepository certPdfRepository;

    @Override
    public Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable) {
        return certInfoRepository.caseSearch(request,certStageList,certStatusList,pageable);
    }

    @Override
    public Boolean batchImport(String examProfileSerialNo,LocalDate examDate, List<CertImportDto> csvData) {
        List<CertInfo> certInfos = checkScv(examProfileSerialNo,examDate,csvData);
        return certInfoRepository.saveAll(certInfos).size() == csvData.size();
    }


    @Override
    public Boolean dispatch(String examProfileSerialNo, CertStage currentStage) {
        if(!currentStage.equals(CertStage.IMPORTED) && !currentStage.equals(CertStage.GENERATED)
                && !currentStage.equals(CertStage.SIGN_ISSUE) && !currentStage.equals(CertStage.NOTIFY)){
            throw new ServiceException(ResultCode.STAGE_ERROR);
        }
        List<CertInfo> list = certInfoRepository.getinfoByNoAndStatus(examProfileSerialNo,currentStage);
        for(CertInfo certInfo : list){
            switch (certInfo.getCertStage()) {
                case IMPORTED -> {
                    certInfo.setCertStage(CertStage.GENERATED);
                    break;
                }
                case GENERATED -> {
                    certInfo.setCertStage(CertStage.SIGN_ISSUE);
                    break;
                }
                case SIGN_ISSUE -> {
                    certInfo.setCertStage(CertStage.NOTIFY);
                    break;
                }
                case NOTIFY -> {
                    certInfo.setCertStage(CertStage.COMPLETED);
                    break;
                }
                default ->{
                    break;
                }
            }
        }
        certInfoRepository.saveAll(list).size();
        return true;
    }

    @Override
    @Transactional
    public void changeCertStatusToInProgress(String examProfileSerialNo, CertStage certStage) {
        List<CertInfo> certInfoList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,certStage,List.of(CertStatus.PENDING,CertStatus.FAILED));
        certInfoList.forEach(cert->cert.setCertStatus(CertStatus.IN_PROGRESS));
        certInfoRepository.saveAll(certInfoList);
    }

    @Override
    @Transactional
    public void batchGeneratePdf(String examProfileSerialNo) throws Exception {

        List<CertInfo> inProgressCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.GENERATED,List.of(CertStatus.IN_PROGRESS));
        byte[] passTemplateInputStream = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_AT_LEAST_ONE_PASS);
        byte[] allFailedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE);
        try{
            for (CertInfo cert : inProgressCertList) {
                this.generatePdf(cert,passTemplateInputStream,allFailedTemplate,true);
            }
        } catch (Exception e){
            inProgressCertList.forEach(cert->{
                if (cert.getCertStatus() != CertStatus.SUCCESS){
                    cert.setCertStatus(CertStatus.FAILED);
                }
            });
            certInfoRepository.saveAll(inProgressCertList);
            throw e;
        }
    }



    private Map<DataFieldName, String> getMergeMapForCert(CertInfo certInfo) throws JsonProcessingException {
        ExamProfile exam = certInfo.getExamProfile();


        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        return docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap);
    }

    private Map<String,List> getTableLoopMapForCert(CertInfo certInfo){
        List<ExamScoreDto> markDtoList = new ArrayList<>();
        if(StringUtils.isNotEmpty(certInfo.getAtGrade())){
            markDtoList.add(new ExamScoreDto("AT",certInfo.getAtGrade()));
        }
        if(StringUtils.isNotEmpty(certInfo.getUcGrade())){
            markDtoList.add(new ExamScoreDto("UC",certInfo.getUcGrade()));
        }
        if(StringUtils.isNotEmpty(certInfo.getUeGrade())){
            markDtoList.add(new ExamScoreDto("UE",certInfo.getUeGrade()));
        }

        if(StringUtils.isNotEmpty(certInfo.getBlnstGrade())) {
            markDtoList.add(new ExamScoreDto("BLNST", certInfo.getBlnstGrade()));
        }

        HashMap<String,List> map = new HashMap<>();
        map.put("examResults",markDtoList);
        return map;
    }

    @Transactional
    @Override
    public void generatePdf(CertInfo certInfo,
                            byte[] atLeastOnePassedTemplate,
                            byte [] allFailedTemplate,
                            boolean isBatchMode) throws Exception {
        logger.info("Start generate.");


        if (!isBatchMode){
            // added this logic to avoid querying db using a bunch of time for the same template in batch mode.
            atLeastOnePassedTemplate = IOUtils.toByteArray(letterTemplateService.getTemplateByNameAsInputStream(LETTER_TEMPLATE_AT_LEAST_ONE_PASS));
            allFailedTemplate = IOUtils.toByteArray(letterTemplateService.getTemplateByNameAsInputStream(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE));
        }

        InputStream appliedTemplate = "P".equals(certInfo.getLetterType())?new ByteArrayInputStream(atLeastOnePassedTemplate):new ByteArrayInputStream(allFailedTemplate);
        byte [] mergedPdf = documentGenerateService.getMergedDocument(appliedTemplate, DocumentOutputType.PDF,getMergeMapForCert(certInfo),getTableLoopMapForCert(certInfo));
        appliedTemplate.close();
        IOUtils.close(appliedTemplate);

        File uploadFileRecord = this.uploadCertPdf(certInfo, mergedPdf);
        this.createCertPdfRecord(certInfo,uploadFileRecord);
        this.updateCertStageAndStatus(certInfo,CertStage.GENERATED,CertStatus.SUCCESS);

        logger.info("Complete generate");
    }


    private void createCertPdfRecord(CertInfo certInfo,File uploadedPdf){
        // Update CertPdf table record
        CertPdf certPdf = new CertPdf();
        certPdf.setCertInfoId(certInfo.getId());
        certPdf.setFileId(uploadedPdf.getId());
        certPdfRepository.save(certPdf);
    }


    private void updateCertStageAndStatus(CertInfo certInfo,CertStage stage, CertStatus status){
        if(stage != null){
            certInfo.setCertStage(stage);
        }

        if(status != null){
            certInfo.setCertStatus(status);
        }

        certInfoRepository.save(certInfo);
    }

    private File uploadCertPdf(CertInfo certInfo, byte[] mergedPdf) throws IOException {

        String processedCertOwnerName = certInfo.getName().trim().replace(" ","_");
        String currentTimeMillisString = String.valueOf(System.currentTimeMillis());
        String savePdfName = String.format("%s_%s_%s.pdf",
                processedCertOwnerName,
                currentTimeMillisString,
                UUID.randomUUID().toString().replace("-","")
        );
        return fileService.uploadFile(FILE_TYPE_CERT_RECORD,certRecordPath,savePdfName,new ByteArrayInputStream(mergedPdf));
    }

    public List<CertInfo> checkScv(String examProfileSerialNo, LocalDate date,List<CertImportDto> csvData){
        Set<String> hkids = new HashSet<>();
        Set<String> passports = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.EXAM_DATE);
        List<CertImportDto> certInfos = CertInfoMapper.INSTANCE.sourceToDestinationList(certInfoRepository.getinfoByNoList(examProfileSerialNo));
        CodeUtil codeUtil = new CodeUtil();
        int count = certInfos.size();
        List<CertInfo> list = new ArrayList<>();
        certInfos.addAll(csvData);
        for(int i = 0; i < certInfos.size(); ++i){
            CertImportDto csv = certInfos.get(i);
            if(i < count ){
                hkids.add(csv.getHkid());
                passports.add(csv.getPassportNo());
            }else{
                LocalDate examDate;
                int row = i+1-count;
                try{
                    examDate = LocalDate.parse(csv.getExamDate(), formatter);
                }catch (Exception e){
                    throw new ServiceException(ResultCode.CSV_EXAM_DATE,row);
                }
                if(!examDate.equals(date)){
                    throw new ServiceException(ResultCode.CSV_EXAM_DATE,row);
                }
                if(csv.getHkid().contains("(") && csv.getHkid().contains("("))
                    csv.setHkid(csv.getHkid().replaceAll("\\)","").replaceAll("\\(",""));
                if(csv.getHkid().isEmpty() && csv.getPassportNo().isEmpty()){
                    throw new ServiceException(ResultCode.HKID_PANNPORT_ABSENT,row);
                }
                if(!csv.getHkid().isEmpty() && !hkids.add(csv.getHkid())){
                    throw new ServiceException(ResultCode.HKID_EXITS,row);
                }
                if(!csv.getPassportNo().isEmpty() && !passports.add(csv.getPassportNo())){
                    throw new ServiceException(ResultCode.PASSPORT_EXITS,row);
                }
                if(!csv.getLetterType().isEmpty() && (!csv.getLetterType().equals("F") && !csv.getLetterType().equals("P"))){
                    throw new ServiceException(ResultCode.CSV_LETTER_TYPE,row);
                }
                if(!codeUtil.validEmai(csv.getEmail())){
                    throw new ServiceException(ResultCode.CSV_EMAIL_ERROR,row);
                }
                //组装batchinfo数据
                CertInfo certInfo = new CertInfo();
                certInfo.setExamProfileSerialNo(examProfileSerialNo);
                certInfo.setHkid(csv.getHkid());
                certInfo.setPassportNo(csv.getPassportNo());
                certInfo.setExamDate(examDate);
                certInfo.setName(csv.getName());
                certInfo.setEmail(csv.getEmail());
                certInfo.setUeGrade(csv.getUeGrade());
                certInfo.setUcGrade(csv.getUcGrade());
                certInfo.setAtGrade(csv.getAtGrade());
                certInfo.setBlnstGrade(csv.getBlnstGrade());
                certInfo.setLetterType(csv.getLetterType());
                certInfo.setCertStatus(CertStatus.PENDING);
                certInfo.setCertStage(CertStage.IMPORTED);
                certInfo.setOnHold(false);
                list.add(certInfo);
            }
        }
        return list;
    }
}
