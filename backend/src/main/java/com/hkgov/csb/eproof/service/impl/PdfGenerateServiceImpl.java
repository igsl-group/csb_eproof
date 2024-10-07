package com.hkgov.csb.eproof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.CertPdfRepository;
import com.hkgov.csb.eproof.dto.ExamScoreDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.CertPdf;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.service.*;
import com.hkgov.csb.eproof.util.DocxUtil;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.model.fields.merge.DataFieldName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.hkgov.csb.eproof.constants.Constants.*;


@Service
public class PdfGenerateServiceImpl implements PdfGenerateService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LetterTemplateService letterTemplateService;
    private final DocumentGenerateService documentGenerateService;
    private final DocxUtil docxUtil;
    private final CertInfoRepository certInfoRepository;
    private final FileService fileService;
    private final CertPdfRepository certPdfRepository;

    @Value("${minio.path.cert-record}")
    private String certRecordPath;


    public PdfGenerateServiceImpl(LetterTemplateService letterTemplateService, DocumentGenerateService documentGenerateService, DocxUtil docxUtil, CertInfoRepository certInfoRepository, FileService fileService, CertPdfRepository certPdfRepository) {
        this.letterTemplateService = letterTemplateService;
        this.documentGenerateService = documentGenerateService;
        this.docxUtil = docxUtil;
        this.certInfoRepository = certInfoRepository;
        this.fileService = fileService;
        this.certPdfRepository = certPdfRepository;
    }

    @Transactional(noRollbackFor = Exception.class)
    @Override
    @Async("generatePdfThreadPool")
    public void singleGeneratePdf(CertInfo certInfo, byte[] atLeastOnePassedTemplate, byte[] allFailedTemplate, boolean isBatchMode, boolean isNewCertInfo) throws Exception {
        try{
            logger.info("Start generate. CertInfo ID: {}",certInfo.getId());

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
            this.updateCertStageAndStatus(certInfo, CertStage.GENERATED, CertStatus.SUCCESS);

            logger.info("Complete generate. CertInfo ID: {}",certInfo.getId());
        } catch(Exception e){
            e.printStackTrace();
            certInfo.setCertStatus(CertStatus.FAILED);
            certInfoRepository.save(certInfo);
        }
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

    public File uploadCertPdf(CertInfo certInfo, byte[] mergedPdf) throws IOException {

        String processedCertOwnerName = certInfo.getName().trim().replace(" ","_");
        String currentTimeMillisString = String.valueOf(System.currentTimeMillis());
        String savePdfName = String.format("%s_%s_%s.pdf",
                processedCertOwnerName,
                currentTimeMillisString,
                UUID.randomUUID().toString().replace("-","")
        );
        return fileService.uploadFile(FILE_TYPE_CERT_RECORD,certRecordPath+"/"+certInfo.getExamProfileSerialNo(),savePdfName,new ByteArrayInputStream(mergedPdf));
    }


    public Map<DataFieldName, String> getMergeMapForCert(CertInfo certInfo) throws JsonProcessingException {
        ExamProfile exam = certInfo.getExamProfile();


        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        // Change the format of date for examMap
        examMap.put("examProfile.examDate",exam.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));
        examMap.put("examProfile.resultLetterDate",exam.getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));

        return docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap);
    }
    public Map<String, List> getTableLoopMapForCert(CertInfo certInfo){
        List<ExamScoreDto> markDtoList = new ArrayList<>();

        if(StringUtils.isNotEmpty(certInfo.getUcGrade())){
            markDtoList.add(new ExamScoreDto("Use of Chinese (UC)",convertGradeToReadableGrade(certInfo.getUcGrade())));
        }
        if(StringUtils.isNotEmpty(certInfo.getUeGrade())){
            markDtoList.add(new ExamScoreDto("Use of English (UE)",convertGradeToReadableGrade(certInfo.getUeGrade())));
        }
        if(StringUtils.isNotEmpty(certInfo.getAtGrade())){
            markDtoList.add(new ExamScoreDto("Aptitude Test (AT)",convertGradeToReadableGrade(certInfo.getAtGrade())));
        }
        if(StringUtils.isNotEmpty(certInfo.getBlnstGrade())) {
            markDtoList.add(new ExamScoreDto("BLNST", convertGradeToReadableGrade(certInfo.getBlnstGrade())));
        }

        if (markDtoList.size() < 4){
            for(int i = markDtoList.size(); i <= 4; i++){
                markDtoList.add(new ExamScoreDto(" "," "));
            }
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

    @Transactional
    public void createCertPdfRecord(CertInfo certInfo,File uploadedPdf){
        // Update CertPdf table record
        CertPdf certPdf = new CertPdf();
        certPdf.setCertInfoId(certInfo.getId());
        certPdf.setFileId(uploadedPdf.getId());
        certPdfRepository.save(certPdf);
    }
}
