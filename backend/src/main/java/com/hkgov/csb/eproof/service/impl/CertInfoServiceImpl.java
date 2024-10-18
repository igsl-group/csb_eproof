package com.hkgov.csb.eproof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.controller.TemplateController;
import com.hkgov.csb.eproof.dao.*;
import com.hkgov.csb.eproof.dto.*;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.entity.enums.CertType;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertInfoMapper;
import com.hkgov.csb.eproof.security.EncryptionUtil;
import com.hkgov.csb.eproof.service.*;
import com.hkgov.csb.eproof.util.CodeUtil;
import com.hkgov.csb.eproof.util.DocxUtil;
import com.hkgov.csb.eproof.util.EProof.EProofConfigProperties;
import com.hkgov.csb.eproof.util.EProof.EProofUtil;
import com.hkgov.csb.eproof.util.EProof.FileUtil;
import com.hkgov.csb.eproof.util.EmailUtil;
import com.hkgov.csb.eproof.util.MinioUtil;
import com.itextpdf.text.pdf.qrcode.WriterException;
import freemarker.template.TemplateException;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.docx4j.model.fields.merge.DataFieldName;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.hkgov.csb.eproof.util.HKIDformatter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.hkgov.csb.eproof.constants.Constants.*;

/**
* @author David
* @description 针对表【cert_info】的数据库操作Service实现
* @createDate 2024-05-10 17:47:40
*/
@Service
@RequiredArgsConstructor
public class CertInfoServiceImpl implements CertInfoService {

    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;
    private final ExamProfileRepository examProfileRepository;
    private final EmailEventRepository emailEventRepository;
    private final EProofConfigProperties eProofConfigProperties;
    private final TemplateController template;
    @Value("${minio.path.cert-record}")
    private String certRecordPath;

    @Value("${minio.path.gcis-batch-xml}")
    private String gcisBatchXmlPath;

    private final EmailUtil emailUtil;

    private final CertInfoRepository certInfoRepository;

    private final SystemParameterRepository systemParameterRepository;
    private final DocumentGenerateService documentGenerateService;
    private final LetterTemplateService letterTemplateService;
    private final DocxUtil docxUtil;
    private final FileService fileService;
    private final CertInfoRenewRepository certInfoRenewRepository;
    private final CertEproofRepository certEproofRepository;
    private final CertPdfRenewRepository certPdfRenewRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final GcisBatchEmailRepository gcisBatchEmailRepository;
    private final CertActionRepository certActionRepository;
    private final PdfGenerateService pdfGenerateService;
    private final CombinedHistoricalResultBeforeRepository  beforeRepository;
    private final GcisEmailServiceImpl gcisEmailServiceImpl;
    private static final Gson GSON = new Gson();
    private final AuthenticationService authenticationService;
    private final HKIDformatter hkidFormatter;

    @Value("${gcis-shared-service.emailWhitelist.enabled}")
    private Boolean whiteListEnabled;
    @Value("${gcis-shared-service.emailWhitelist.toList}")
    private List<String> whiteList;

    @Value("${document.qr-code.height}")
    private Integer qrCodeHeight;

    @Value("${document.qr-code.width}")
    private Integer qrCodeWidth;

    @Value("${document.qr-code.x}")
    private Integer qrCodeX;

    @Value("${document.qr-code.y}")
    private Integer qrCodeY;

    @Value("${gcis-shared-service.batch-email.split-size}")
    private Integer splitSize;

    @Value("${eproof-config.issuance-split-size}")
    private Integer issuanceSplitSize;

    @Value("eproof-config.hkid-salt-id")
    private String saltId;

    @Value("eproof-config.hkid-salt-value")
    private String saltValue;

    @Value("eproof-config.hkid-salt-uuid")
    private String saltUuid;

    @Value("eproof-config.hkid-salt-sdid")
    private String saltSdid;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CertPdfRepository certPdfRepository;

    @Override
    @Transactional
    public Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable) {
        return certInfoRepository.certSearch(request,certStageList,certStatusList,pageable);
    }

    @Override
    public Boolean batchImport(String examProfileSerialNo, List<CertImportDto> csvData) throws Exception {
        List<CertInfo> certInfos = checkScv(examProfileSerialNo,csvData);
        return certInfoRepository.saveAll(certInfos).size() == csvData.size();
    }


    @Override
    public Boolean dispatch(String examProfileSerialNo, CertStage currentStage) {
        if(!currentStage.equals(CertStage.IMPORTED) && !currentStage.equals(CertStage.GENERATED)
                && !currentStage.equals(CertStage.SIGN_ISSUE) && !currentStage.equals(CertStage.NOTIFY)){
            throw new ServiceException(ResultCode.STAGE_ERROR);
        }
/*
        List<CertInfo> list = certInfoRepository.getinfoByNoAndStatus(examProfileSerialNo,currentStage);
        if(list.isEmpty()){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
*/

        CertStage nextStage = null;
        switch (currentStage){
            case IMPORTED -> {
                nextStage = CertStage.GENERATED;
                certInfoRepository.dispatchCert(examProfileSerialNo,currentStage,nextStage,CertStatus.PENDING,false,authenticationService.getCurrentUser().getDpUserId());
                break;
            }
            case GENERATED -> {
                nextStage = CertStage.SIGN_ISSUE;
                certInfoRepository.dispatchCert(examProfileSerialNo,currentStage,nextStage,CertStatus.PENDING,false,authenticationService.getCurrentUser().getDpUserId());
                break;
            }
            case SIGN_ISSUE -> {
                nextStage = CertStage.NOTIFY;
                certInfoRepository.dispatchCert(examProfileSerialNo,currentStage,nextStage,CertStatus.PENDING,false,authenticationService.getCurrentUser().getDpUserId());
                break;
            }
            case NOTIFY -> {
                nextStage = CertStage.COMPLETED;
                certInfoRepository.dispatchCert(examProfileSerialNo,currentStage,nextStage,CertStatus.SUCCESS,true,authenticationService.getCurrentUser().getDpUserId());
                break;
            }
            default ->{throw new ServiceException(ResultCode.STAGE_ERROR);}
        }
//            20240904 Improve dispatch performance
        /*for(CertInfo certInfo : list){
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
            certInfo.setCertStage(nextStage);
            certInfo.setCertStatus(CertStatus.PENDING);
        }
        certInfoRepository.saveAll(list);*/

        switch (currentStage) {
            case IMPORTED -> {
                EmailTemplate emailTemplate = emailTemplateRepository.findByName("Import_Dispatch");
                gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
                break;
            }
            case GENERATED -> {
                EmailTemplate emailTemplate = emailTemplateRepository.findByName("Generate_Dispatch");
                gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
                break;
            }
            case SIGN_ISSUE -> {
                EmailTemplate emailTemplate = emailTemplateRepository.findByName("Sign_and_Issue_Dispatch");
                gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
                break;
            }
            default ->{
                break;
            }
        }
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
    public List<CertInfo> changeCertStatusToScheduled(String examProfileSerialNo, CertStage certStage) {
        List<CertInfo> certInfoList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,certStage,List.of(CertStatus.PENDING,CertStatus.FAILED));
        certInfoList.forEach(cert->cert.setCertStatus(CertStatus.SCHEDULED));
        certInfoRepository.saveAll(certInfoList);
        return certInfoList;
    }

    @Override
    @Transactional
    public Boolean  batchScheduleCertSignAndIssue(String examProfileSerialNo) {

        List<CertInfo> alreadyScheduledCert = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.SIGN_ISSUE,List.of(CertStatus.SCHEDULED));
        if (!alreadyScheduledCert.isEmpty()){
            return false;
        }

        certInfoRepository.batchScheduledSignAndIssue(examProfileSerialNo,CertStage.SIGN_ISSUE.name(),List.of(CertStatus.IN_PROGRESS.name(),CertStatus.PENDING.name()),CertStatus.SCHEDULED.name(),authenticationService.getCurrentUser().getDpUserId(), issuanceSplitSize);
       /* List<CertInfo> inProgressCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.SIGN_ISSUE,List.of(CertStatus.IN_PROGRESS));
        List<CertInfo> pendingSignAndIssueCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.SIGN_ISSUE,List.of(CertStatus.PENDING));

        List<CertInfo> toBeScheduledCert = new ArrayList<>();
        toBeScheduledCert.addAll(inProgressCertList);
        toBeScheduledCert.addAll(pendingSignAndIssueCertList);

        toBeScheduledCert.forEach(cert->{
            cert.setCertStatus(CertStatus.SCHEDULED);
        });

        certInfoRepository.saveAll(toBeScheduledCert);*/

        // Set access token to null to force the following action to get a new access token
        eProofConfigProperties.setAccessToken(null);

        return true;
    }

    @Override
//    @Transactional(noRollbackFor = Exception.class)
    @Async
    public void batchGeneratePdf(String examProfileSerialNo) throws Exception {

        List<CertInfo> inProgressCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.GENERATED,List.of(CertStatus.IN_PROGRESS));
        byte[] passTemplateInputStream = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_AT_LEAST_ONE_PASS);
        byte[] allFailedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE);
//        try{
        for (CertInfo cert : inProgressCertList) {
            pdfGenerateService.singleGeneratePdf(cert,passTemplateInputStream,allFailedTemplate,true,false);
        }

        inProgressCertList = inProgressCertList.stream().filter(certInfo->CertStatus.FAILED.equals(certInfo.getCertStatus())).toList();

        if(!inProgressCertList.isEmpty()){
            logger.info("Found {} failed generate cert in first round. Start second round.",inProgressCertList.size());
            for (CertInfo certInfo : inProgressCertList) {
                pdfGenerateService.singleGeneratePdf(certInfo,passTemplateInputStream,allFailedTemplate,true,false);
            }
        }


        inProgressCertList = inProgressCertList.stream().filter(certInfo->CertStatus.FAILED.equals(certInfo.getCertStatus())).toList();

        if(!inProgressCertList.isEmpty()){
            logger.info("Found {} failed generate cert in second round. Start third round.",inProgressCertList.size());
            for (CertInfo certInfo : inProgressCertList) {
                pdfGenerateService.singleGeneratePdf(certInfo,passTemplateInputStream,allFailedTemplate,true,false);
            }
        }
        /*} catch (Exception e){
            inProgressCertList.forEach(cert->{
                if (cert.getCertStatus() != CertStatus.SUCCESS){
                    cert.setCertStatus(CertStatus.FAILED);
                }
            });
            certInfoRepository.saveAll(inProgressCertList);
            throw e;
        }*/


    }

    private Map<DataFieldName, String> getMergeMapForCert(CertInfo certInfo) throws JsonProcessingException {
        ExamProfile exam = certInfo.getExamProfile();


        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        // Change the format of date for examMap
        examMap.put("examProfile.examDate",exam.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));
        examMap.put("examProfile.resultLetterDate",exam.getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));

        return docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap);
    }

    private Map<String,List> getTableLoopMapForCert(CertInfo certInfo){
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
            for(int i = markDtoList.size(); i < 4; i++){
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

    @Transactional(noRollbackFor = Exception.class)
    @Override
    @Async("generatePdfThreadPool")
    public void  singleGeneratePdf(CertInfo certInfo,
                                  byte[] atLeastOnePassedTemplate,
                                  byte [] allFailedTemplate,
                                  boolean isBatchMode,boolean isNewCertInfo) throws Exception {

        try{
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
        } catch(Exception e){
            e.printStackTrace();
            certInfo.setCertStatus(CertStatus.FAILED);
            certInfoRepository.save(certInfo);

        }

    }

    @Override
    public Boolean updateEmail(UpdateEmailDto updateEmailDto) {
        List<CertInfo> certInfos = new ArrayList<>();

        if(StringUtils.isBlank(updateEmailDto.getCurrentHkid()) && StringUtils.isBlank(updateEmailDto.getCurrentPassport())){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }

        if(StringUtils.isNotBlank(updateEmailDto.getCurrentHkid())){
            certInfos = certInfoRepository.findAllByHkid(updateEmailDto.getCurrentHkid());
        }
        if(StringUtils.isBlank(updateEmailDto.getCurrentHkid()) && StringUtils.isNotBlank(updateEmailDto.getCurrentPassport())){
            certInfos = certInfoRepository.findAllByPassport(updateEmailDto.getCurrentPassport());
        }
        if(certInfos.isEmpty()){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        certInfos.forEach(x -> {
            x.setEmail(updateEmailDto.getEmail());
        });
        List<CertInfo> newCertInfos = certInfoRepository.saveAll(certInfos);
        return certInfos.size()==newCertInfos.size();
    }

    @Override
    public Boolean updatePersonalParticular(UpdatePersonalDto personalDto) {
        List<CertInfo> certInfos = new ArrayList<>();

        if(StringUtils.isBlank(personalDto.getCurrentHkid()) && StringUtils.isBlank(personalDto.getCurrentPassport())){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        if(StringUtils.isNotBlank(personalDto.getCurrentHkid())){
            certInfos = certInfoRepository.findAllByHkid(personalDto.getCurrentHkid());
        }
        if(StringUtils.isBlank(personalDto.getCurrentHkid()) && StringUtils.isNotBlank(personalDto.getCurrentPassport())){
            certInfos = certInfoRepository.findAllByPassport(personalDto.getCurrentPassport());
        }
        if(certInfos.isEmpty()){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }


        List<CertInfoRenew> addList = new ArrayList<>();
        certInfos.forEach(x -> {
            addList.add(addCertInfoRenew(x,personalDto));
        });
        return certInfoRenewRepository.saveAll(addList).size() == certInfos.size();
    }

    @Override
    public void updatePersonalParticularById(Long certInfoId,UpdatePersonalDto personalDto) {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElse(null);
        if(Objects.isNull(certInfo)){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        CertInfoRenew certInfoRenew = addCertInfoRenew(certInfo,personalDto);
        certInfoRenewRepository.save(certInfoRenew);
    }

    @Override
    public Boolean updateResult(Long certInfoId, UpdateResultDto resultDto) {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElse(null);
        if(Objects.isNull(certInfo)){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        CertInfoRenew infoRenew = new CertInfoRenew();
        if(CertStage.VOIDED.equals(infoRenew.getCertStage())){
            throw new GenericException(ExceptionEnums.CERT_INFO_VOIDED);
        }

        infoRenew.setNewHkid(certInfo.getHkid());
        infoRenew.setOldHkid(certInfo.getHkid());
        infoRenew.setNewPassport(certInfo.getPassportNo());
        infoRenew.setOldPassport(certInfo.getPassportNo());
        infoRenew.setNewEmail(certInfo.getEmail());
        infoRenew.setOldEmail(certInfo.getEmail());
//        infoRenew.setNewCname(certInfo.getCname());
//        infoRenew.setOldCname(certInfo.getCname());
        infoRenew.setNewName(certInfo.getName());
        infoRenew.setOldName(certInfo.getName());
        infoRenew.setCertInfoId(certInfo.getId());
        infoRenew.setOldLetterType(certInfo.getLetterType());
        infoRenew.setNewLetterType(resultDto.getNewLetterType());
        infoRenew.setOldBlGrade(certInfo.getBlnstGrade());
        infoRenew.setOldUcGrade(certInfo.getUcGrade());
        infoRenew.setOldUeGrade(certInfo.getUeGrade());
        infoRenew.setOldAtGrade(certInfo.getAtGrade());
        infoRenew.setNewBlGrade(resultDto.getNewBlnstGrade());
        infoRenew.setNewUcGrade(resultDto.getNewUcGrade());
        infoRenew.setNewUeGrade(resultDto.getNewUeGrade());
        infoRenew.setNewAtGrade(resultDto.getNewAtGrade());

        infoRenew.setRemark(resultDto.getRemark());
        infoRenew.setType(CertType.RESULT_UPDATE);
        infoRenew.setCertStage(CertStage.GENERATED);
        infoRenew.setCertStatus(CertStatus.PENDING);
        infoRenew.setIsDelete(false);
        certInfoRenewRepository.save(infoRenew);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSignAndIssue(String examProfileSerialNo) {
        List<CertInfo> signAndIssueInProgressCert = certInfoRepository.getCertByExamSerialAndStageAndStatus(
                examProfileSerialNo,
                CertStage.SIGN_ISSUE,
                List.of(CertStatus.IN_PROGRESS)
        );

        try{
            signAndIssueInProgressCert.forEach(this::singleSignAndIssue);
        }catch(Exception e){
            signAndIssueInProgressCert.forEach(x -> {
                if(x.getCertStatus().equals(CertStatus.PENDING)){
                    x.setCertStatus(CertStatus.FAILED);
                }
            });
            certInfoRepository.saveAll(signAndIssueInProgressCert);
        }

    }

    @Override
    public void resume(Long certInfoId, String remark) {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElse(null);
        if(Objects.isNull(certInfo)){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        boolean updateFlag = CertStage.IMPORTED.equals(certInfo.getCertStage())
                || CertStage.GENERATED.equals(certInfo.getCertStage()) || CertStage.SIGN_ISSUE.equals(certInfo.getCertStage());
        if(!updateFlag){
            throw new GenericException(ExceptionEnums.CERT_INFO_NOT_UPDATE);
        }
        certInfo.setOnHold(false);
        certInfo.setOnHoldRemark(remark);
        certInfoRepository.save(certInfo);

        EmailTemplate emailTemplate = emailTemplateRepository.findByName("Exam_Profile_Case_Resume");
        gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
    }

    @Override
    public void hold(Long certInfoId, String remark) {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElse(null);
        if(Objects.isNull(certInfo)){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        boolean updateFlag = CertStage.IMPORTED.equals(certInfo.getCertStage())
                || CertStage.GENERATED.equals(certInfo.getCertStage()) || CertStage.SIGN_ISSUE.equals(certInfo.getCertStage());
        if(!updateFlag){
            throw new GenericException(ExceptionEnums.CERT_INFO_NOT_UPDATE);
        }
        certInfo.setOnHold(true);
        certInfo.setOnHoldRemark(remark);
        certInfoRepository.save(certInfo);

        EmailTemplate emailTemplate = emailTemplateRepository.findByName("Exam_Profile_Case_Onhold");
        gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
    }

    @Override
    public void delete(Long certInfoId) {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElse(null);
        if(Objects.isNull(certInfo)){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }

        // this api
        if(!certInfo.getOnHold()){
            throw new GenericException(ExceptionEnums.CERT_INFO_NOT_DELETE);
        }
        // TODO: 5/8/2024
        // if stage = issue and status = complete
        // please revoke the cert as well
        // This api can be call when email not yet to send
       certInfoRepository.delete(certInfo);
    }

    @Override
    public CertInfo getNextScheduledSignAndIssueCert(String examProfileSerialNo) {
        CertInfo certInfo = certInfoRepository.getNextScheduledSignAndIssueCert(examProfileSerialNo);
        if(certInfo != null){
            certInfo.setCertStatus(CertStatus.IN_PROGRESS);
            certInfoRepository.save(certInfo);
        }
        return certInfo;
    }

    @Override
    public void notifyInternalUserSignAndIssueCompleted(String examProfileSerialNo) throws TemplateException, IOException {
        EmailTemplate emailTemplate = emailTemplateRepository.findByName("Sign_and_Issue_Completed");

        Map<String,Object> map = new HashMap<>();
        map.put("examProfileSerialNo", examProfileSerialNo);
        String htmlBody = emailUtil.getRenderedHtml(emailTemplate.getBody(),map);
        gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), htmlBody);
    }

    @Override
    @Transactional
    public void uploadSignedPdf(Long certInfoId, MultipartFile file) {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElse(null);
        if(Objects.isNull(certInfo)){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
        if(!CertStage.SIGN_ISSUE.equals(certInfo.getCertStage()) && !CertStatus.IN_PROGRESS.equals(certInfo.getCertStatus())){
            throw new GenericException(ExceptionEnums.CERT_INFO_NOT_UPDATE);
        }

        try{

            certInfo.setActualSignTime(LocalDateTime.now());
            certInfoRepository.save(certInfo);

//            File uploadedPdf = fileService.uploadFile(FILE_TYPE_CERT_RECORD,certRecordPath,file.getName(),file.getInputStream());
            deleteCertPdf(certInfo);
            File uploadedPdf = this.uploadCertPdf(certInfo, file.getBytes());
            this.createCertPdfRecord(certInfo,uploadedPdf);

        }catch (IOException e){
            throw new GenericException(ExceptionEnums.CANNOT_UPLOAD_SIGNED_PDF_FOR_CERT);
        }
    }

    @Override
    public void issueCert(Long certInfoId) throws Exception {


        CertEproof certEproof = certEproofRepository.findByCertInfoId(certInfoId);

      /*  CertEproof certEproof = null;
        if(certEproofList!=null && !certEproofList.isEmpty()){
            certEproof = certEproofList.get(0);
        }*/
       /* if(!CertStage.SIGN_ISSUE.equals(certInfo.getCertStage()) && !CertStatus.SUCCESS.equals(certInfo.getCertStatus())){
            throw new GenericException(ExceptionEnums.CERT_INFO_NOT_UPDATE);
        }*/

        CertInfo certInfo = certInfoRepository.findById(certInfoId).get();

//        File certPdf = certInfo.getPdfList()!=null&&certInfo.getPdfList().size()>0?certInfo.getPdfList().get(0):null;
        File certPdf = fileRepository.getLatestPdfForCert(certInfo.getId());

        byte[] certPdfBinary = minioUtil.getFileAsByteArray(certPdf.getPath());


        EProofUtil.issuePdf(certEproof.getUuid(),EProofUtil.calcPdfHash(certPdfBinary));

        certInfo.setCertStage(CertStage.SIGN_ISSUE);
        certInfo.setCertStatus(CertStatus.SUCCESS);

        certInfoRepository.save(certInfo);
    }

    @Override
    public String prepareEproofUnsignJson(Long certInfoId) throws NoSuchAlgorithmException{
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElseThrow(()->new EntityNotFoundException("Cert info with provided id not found. Cert info ID: "+certInfoId));



        String issueToEn = certInfo.getName();
        String issueToTc = certInfo.getName();
        String issueToSc = certInfo.getName();
        String eproofType = "personal";

        String  en1, en2, en3,
                tc1, tc2, tc3,
                sc1, sc2, sc3;



        Map<String, String> extraInfo = new HashMap<>();
        extraInfo.put("cert_info_id", certInfo.getId().toString());
        extraInfo.put("exam_profile_serial_no", certInfo.getExamProfileSerialNo());
        extraInfo.put("result_letter_date", certInfo.getExamProfile().getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));
        extraInfo.put("candidate_name", certInfo.getName());
        extraInfo.put("exam_date", certInfo.getExamProfile().getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));

        extraInfo.put("paper_1", StringUtils.isNotEmpty(certInfo.getUcGrade())? "Use of Chinese (UC)" : "");
        extraInfo.put("result_1", certInfo.getUcGrade());

        extraInfo.put("paper_2", StringUtils.isNotEmpty(certInfo.getUeGrade()) ? "Use of English (UE)" : "");
        extraInfo.put("result_2", certInfo.getUeGrade());

        extraInfo.put("paper_3", StringUtils.isNotEmpty(certInfo.getAtGrade()) ? "Aptitude Test (AT)" : "");
        extraInfo.put("result_3", certInfo.getAtGrade());

        extraInfo.put("paper_4", StringUtils.isNotEmpty(certInfo.getBlnstGrade())? "BLNST" : "");
        extraInfo.put("result_4", certInfo.getBlnstGrade());

        extraInfo.put("hkid_or_passport", certInfo.getHkidOrPassport());

        LocalDateTime expiryDate = LocalDateTime.of(2099,12,31,23,59,59);
        LocalDateTime issueDate = LocalDateTime.now();

        String eproofId = certInfo.getEproofId();

        String eproofTemplateCode;
        if("P".equals(certInfo.getLetterType())){
            eproofTemplateCode = "CSBEPROOF";
        }else{
            eproofTemplateCode = "CSBEPROOFFAIL";
        }

        int majorVersion = 1;
        en1 = certInfo.getName();
        en2 = certInfo.getExamDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        en3 = "Common Recruitment Examination / Basic Law and National Security Law Test (Degree / Professional Grades)";
        tc1 = certInfo.getName();
        tc2 = certInfo.getExamDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        tc3 = "綜合招聘考試及《基本法及香港國安法》測試（學位/專業程度職系）";
        sc1 = certInfo.getName();
        sc2 = certInfo.getExamDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        sc3 = "综合招聘考试及《基本法及香港国安法》测试（学位/专业程度职系）";

        return EProofUtil.getUnsignedEproofJson(
                null,
                issueDate,
                eproofId,
                eproofTemplateCode,
                majorVersion,
                tc1,
                tc2,
                tc3,
                sc1,
                sc2,
                sc3,
                en1,
                en2,
                en3,
                extraInfo,
                EProofUtil.type.personal,
                certInfo.getHkid()
        );
    }

    @Override
    public byte[] prepareEproofPdf(Long certInfoId, PrepareEproofPdfRequest prepareEproofPdfRequest) throws Exception {

        CertInfo certInfo = certInfoRepository.findById(certInfoId).get();
         //Register the json to get the UUID from EProof
        String uuid = null;
        String publicKey = prepareEproofPdfRequest.getPublicKey();
        logger.info(publicKey);
        String keyName = systemParameterRepository.findByName(publicKey).orElseThrow(() -> new GenericException("public.key.not.found","Public key not found.")).getValue();
        String eproofTypeId = null;

        if("P".equals(certInfo.getLetterType())){
            eproofTypeId = eProofConfigProperties.getPassTemplateTypeId();
        } else {
            eproofTypeId = eProofConfigProperties.getFailTemplateTypeId();
        }
        LocalDateTime downloadExpiryDateTime = LocalDateTime.of(2099,12,31,23,59,59);

        Map<String, Object> registerResult = EProofUtil.registerOrUpdateEproof(
                uuid,
                prepareEproofPdfRequest.getEproofDataJson(),
                prepareEproofPdfRequest.getSignedProofValue(),
                keyName,
                eproofTypeId,
                -1,
                downloadExpiryDateTime,
                certInfo.getExamProfile().getEffectiveDate().atTime(0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+08:00")),
                certInfo.getHkid()
        );

        logger.info("[registerResult]" + GSON.toJson(registerResult));


        uuid = (String) registerResult.get("uuid");
        Integer returnVersion = (Integer) registerResult.get("version");
        String token = (String) registerResult.get("token");


        logger.debug("[KeyName]" + keyName);
        logger.debug("[uuid]" + uuid);
        logger.debug("[returnVersion]" + returnVersion);


        // Get QR code string from eProof
        String qrCodeString = EProofUtil.getQrCodeString(
                (String) registerResult.get("eProofJson"),
                uuid,
                returnVersion,
                null,
                -1
        );

        logger.debug("[qrCodeString]" + qrCodeString);

        CertEproof certEproof = certEproofRepository.findByCertInfoId(certInfoId);

        if(certEproof != null){
            //TODO Signed cert and sign again
            logger.info("Found existing cert_eproof record. Update cert_eproof record. Cert name: "+certInfo.getName());

            updateCertEproofRecord(
                    certEproof,
                    uuid,
                    returnVersion,
                    token,
                    (String)registerResult.get("eProofJson"),
                    "",
                    eProofConfigProperties.getDownloadUrlPrefix()+ URLEncoder.encode(token, StandardCharsets.UTF_8),
                    keyName,
                    certInfo.getEproofId(),
                    qrCodeString
            );
            //            throw new GenericException(ExceptionEnums.CERT_EPROOF_EXISTING_RECORD_FOUND);
        } else{
            //Create CertEproof record with response from eProof
            createCertEproofRecord(
                    certInfoId,
                    uuid,
                    returnVersion,
                    token,
                    (String)registerResult.get("eProofJson"),
                    "",
                    eProofConfigProperties.getDownloadUrlPrefix()+ URLEncoder.encode(token, StandardCharsets.UTF_8),
                    keyName,
                    certInfo.getEproofId(),
                    qrCodeString
            );
        }

        // Update the PDF
        File latestCert = fileRepository.getLatestPdfForCert(certInfoId);
        InputStream is = minioUtil.getFileAsStream(latestCert.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PDDocument pdDocument = PDDocument.load(is)) {
            // Retrieve the document information
            PDDocumentInformation info = pdDocument.getDocumentInformation();
            // Set the title and author
            String pdfTitle = "";

            if(certInfo.getPassed() != null && certInfo.getPassed()){
                pdfTitle = "Passed cert";
            }else{
                pdfTitle = "Failed cert";
            }
            info.setTitle(pdfTitle);
            info.setAuthor(eProofConfigProperties.getIssuerNameEn());

            // Generate the QR code image
            byte [] qrCodeImageBinary = generateQrCodeBinary(qrCodeString);

            PDImageXObject qrCodeImage = PDImageXObject.createFromByteArray(pdDocument, qrCodeImageBinary, "QR Code");
            try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdDocument.getPage(0), PDPageContentStream.AppendMode.APPEND, true)) {
                contentStream.drawImage(qrCodeImage, qrCodeX, qrCodeY, qrCodeWidth, qrCodeHeight);
            }

            String pdfKeyword = "";

            pdfKeyword = EProofUtil.getPdfKeyword(uuid, returnVersion, keyName, qrCodeString);

            info.setKeywords(pdfKeyword);

            pdDocument.save(baos);
        }
        baos.close();

        // Upload the updated PDF
//        minioUtil.uploadFile(latestCert.getPath(), baos);

        // return the binary array
        return baos.toByteArray();
        //Completed preparing for Eproof PDF
    }

    public byte[] generateQrCodeBinary(String qrCodeString) throws WriterException, com.google.zxing.WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeString, BarcodeFormat.QR_CODE,qrCodeWidth,qrCodeHeight);
        BufferedImage bi = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(bi,"png",baos);
        baos.close();
        return baos.toByteArray();
    }

    private CertEproof updateCertEproofRecord(CertEproof certEproof,
                                        String uuid,
                                        Integer version,
                                        String token,
                                        String eWalletJson,
                                        String eCertHtml,
                                        String url,
                                        String keyName,
                                        String eproofId,
                                        String qrCodeString) {
        certEproof.setEproofId(eproofId);
        certEproof.setKeyName(keyName);
        certEproof.setUuid(uuid);
        certEproof.setVersion(version);
        certEproof.setToken(token);
        certEproof.setEWalletJson(eWalletJson);
        certEproof.setECertHtml(eCertHtml);
        certEproof.setUrl(url);
        certEproof.setQrCodeString(qrCodeString);
        certEproofRepository.save(certEproof);

        return certEproof;
    }

    @Override
    public void insertGcisBatchEmail(String examProfileSerialNo, InsertGcisBatchEmailDto insertGcisBatchEmailDto) throws DocumentException, IOException, TemplateException {

        List<CertInfo> certInfoList = certInfoRepository.getToBeSendBatchEmailCert(examProfileSerialNo);
        List<List<CertInfo>> choppedCertInfo2dList = splitCertInfoList(certInfoList, splitSize);

        EmailTemplate notifyEmailTemplate = emailTemplateRepository.findByName(Constants.EMAIL_TEMPLATE_NOTIFY);

        String convertedEmailTemplate = convertEmailTemplateToGcisBatchEmailTemplate(notifyEmailTemplate.getBody());
        SystemParameter xmlTemplateLocation = systemParameterRepository.findByName(Constants.SYS_PARAM_NOTI_BATCH_XML_LOCATION).get();

        if(xmlTemplateLocation == null){
            throw new GenericException("xml.template.location.not.found","XML template location not found.");
        }

        byte [] xmlByteArray = minioUtil.getFileAsByteArray(xmlTemplateLocation.getValue());


        SAXReader reader = new SAXReader();

        ExamProfile examProfile = examProfileRepository.findById(examProfileSerialNo).get();

        int i=1;
        for (List<CertInfo> choppedCertInfoList : choppedCertInfo2dList) {
            String listName = generateListName(examProfileSerialNo,i);
            String processedXml = processBatchEmailXml(notifyEmailTemplate.getSubject(),convertedEmailTemplate, listName,examProfile,reader,xmlByteArray,choppedCertInfoList);

            File file =  fileService.uploadFile(FILE_TYPE_GCIS_BATCH_XML,gcisBatchXmlPath,listName + ".xml",new ByteArrayInputStream(processedXml.getBytes() ));


            GcisBatchEmail gcisBatchEmail = this.createGcisBatchEmail(insertGcisBatchEmailDto,notifyEmailTemplate,processedXml,listName, file.getId());
            choppedCertInfoList.forEach(certInfo -> {
                certInfo.setGcisBatchEmailId(gcisBatchEmail.getId());
            });
            i++;
            certInfoRepository.saveAll(choppedCertInfoList);
        }
    }

    private String convertEmailTemplateToGcisBatchEmailTemplate(String templateBody) throws TemplateException, IOException {
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("application_name","*#*application_name*#*");
        replaceMap.put("examination_date","*#*examination_date*#*");
        replaceMap.put("eproof_document_url","*#*eproof_document_url*#*");
        return emailUtil.getRenderedHtml(templateBody,replaceMap);
    }

    private String generateListName(String examProfileSerialNo, int loopIndex) {
        return String.format("%s%s%s"
                ,examProfileSerialNo
                ,LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_3))
                ,loopIndex
        );
    }
    private String processBatchEmailXml(String emailTitle,String convertedEmailTemplate, String listName, ExamProfile examProfile, SAXReader reader, byte[] xmlByteArray, List<CertInfo> choppedCertInfoList) throws DocumentException, IOException {


        Document document = reader.read(new ByteArrayInputStream(xmlByteArray));
        Element root = document.getRootElement();

        // Update <NOTI_LIST> content
        Element notiList = root.element("NOTI_LIST");
        notiList.element("NOTI_LIST_NAME").setText(listName);
        notiList.element("TEML_NAME").setText(listName);

        // Update <NOTI_LIST_NAME> for each merge item
        List<Element> notiMergItemList= root.elements("NOTI_MERG_ITEM");
        for (Element element : notiMergItemList) {
            element.element("NOTI_LIST_NAME").setText(listName);
        }

        // Update TEMPLATE content
        Element template = root.element("TEMPLATE");
        template.element("TEML_NAME").setText(listName);
        template.element("NOTI_SUBJ").setText(emailTitle);
        template.element("NOTI_CONT").setText(convertedEmailTemplate);
        template.element("NOTI_LIST_NAME").setText(listName);

        // Add <SUBR_MERG_ITEM> content
        for (CertInfo certInfo : choppedCertInfoList) {
            if(whiteListEnabled && whiteList != null && !whiteList.contains(certInfo.getEmail())){
                // skip adding this recipient since he/she is not in the white list
                continue;
            }

            Element recipient = root.addElement("SUBR_MERG_ITEM");
            recipient.addElement("ACTION").setText("Create");
            recipient.addElement("NOTI_LIST_NAME").setText(listName);
            recipient.addElement("ADDRESS").setText(certInfo.getEmail());

            // TODO: Temporary add 10 attachment xml according to the template.
            //  If attachment xml section is not needed, this part can be removed.
            for (int i=1;i<=10;i++){
                if(i<10){
                    recipient.addElement("SUBR_ATTH_0"+i);
                }
                else{
                    recipient.addElement("SUBR_ATTH_"+i);
                }
            }

            Element candidateName = recipient.addElement("MERG_ITEM");
            candidateName.addElement("MERG_ITEM_NAME").setText("application_name");
            candidateName.addElement("MERG_ITEM_VALUE").setText(certInfo.getName());

            Element examDate = recipient.addElement("MERG_ITEM");
            examDate.addElement("MERG_ITEM_NAME").setText("examination_date");
            examDate.addElement("MERG_ITEM_VALUE").setText(examProfile.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));

            Element eproofDocumentUrl = recipient.addElement("MERG_ITEM");
            eproofDocumentUrl.addElement("MERG_ITEM_NAME").setText("eproof_document_url");

            eproofDocumentUrl.addElement("MERG_ITEM_VALUE").setText(certInfo.getCertEproof()!= null? certInfo.getCertEproof().getUrl():"");


        }



        String processedXml = document.asXML();
        return beautifyXml(processedXml);
    }

    private String beautifyXml(String processedXml) throws IOException, DocumentException {
        StringWriter stringWriter = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(stringWriter, format);
        writer.write(new org.dom4j.io.SAXReader().read(new java.io.StringReader(processedXml)));
        writer.close();
        return stringWriter.toString();
    }

    @Override

    public void approveRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception {
        // Set the eproof config properties to hard refresh the token
        eProofConfigProperties.setAccessToken(null);


        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));
        List<CertInfo> toBeRevokeCertInfoList = certAction.getCertInfos();

        for (CertInfo certInfo : toBeRevokeCertInfoList) {
            this.actualRevokeWithEproofModule(certInfo.getId(), certApproveRejectRevokeDto.getRemark());
        }
        certAction.setRemark(certApproveRejectRevokeDto.getRemark());
        certAction.setCanEmailAddress(certApproveRejectRevokeDto.getEmailTarget());
        certAction.setCanEmailSubject(certApproveRejectRevokeDto.getEmailSubject());
        certAction.setCanEmailContent(certApproveRejectRevokeDto.getEmailContent());
        certAction.setStatus(CertStatus.APPROVED);
        certActionRepository.save(certAction);
        //TODO Send email
        gcisEmailServiceImpl.sendTestEmail(certApproveRejectRevokeDto.getEmailTarget(), certApproveRejectRevokeDto.getEmailSubject(), certApproveRejectRevokeDto.getEmailContent());
    }

    public void rejectRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception {
        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));

        certAction.setRemark(certApproveRejectRevokeDto.getRemark());
        certAction.setCanEmailAddress(certApproveRejectRevokeDto.getEmailTarget());
        certAction.setCanEmailSubject(certApproveRejectRevokeDto.getEmailSubject());
        certAction.setCanEmailContent(certApproveRejectRevokeDto.getEmailContent());
        certAction.setStatus(CertStatus.REJECTED);
        certActionRepository.save(certAction);

        EmailTemplate emailTemplate = emailTemplateRepository.findByName("Revoke_Reject");
        gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
    }

    public void resubmitRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception {
        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));

        certAction.setRemark(certApproveRejectRevokeDto.getRemark());
        certAction.setCanEmailAddress(certApproveRejectRevokeDto.getEmailTarget());
        certAction.setCanEmailSubject(certApproveRejectRevokeDto.getEmailSubject());
        certAction.setCanEmailContent(certApproveRejectRevokeDto.getEmailContent());
        certAction.setStatus(CertStatus.PENDING);
        certActionRepository.save(certAction);

        EmailTemplate emailTemplate = emailTemplateRepository.findByName("Revoke_Request");
        gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
    }

    public void removeRevoke(Long certActionId) throws Exception {
        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));
        certAction.setStatus(CertStatus.WITHDRAWAL);
        certActionRepository.save(certAction);
    }

    @Override
    public void actualRevokeWithEproofModule(Long certInfoId, String remark) throws Exception {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).orElseThrow(()->new GenericException("cert.info.not.found","Cert info not found."));
        CertEproof certEproof = certInfo.getCertEproof();
        if (certEproof != null) {
            EProofUtil.revokeEproof(certEproof.getUuid());
        }
        certInfo.setValid(false);
        certInfo.setRemark(remark);
        certInfo.setRevokeDate(LocalDate.from(LocalDateTime.now()));
        certInfoRepository.save(certInfo);
    }

    @Override
    public ResponseEntity<byte[]> enquiryResult(List<String> params) {
        List<String> hkids = params.stream()
                .map(s -> s.replaceAll("\\)","").replaceAll("\\(",""))
                .map(s -> s.replaceAll("\\s",""))
                .collect(Collectors.toList());
        List<CertInfo> certInfos = certInfoRepository.findByHkidIn(hkids,params);
        List<CombinedHistoricalResultBefore> befores = beforeRepository.findByHkidIn(hkids,params);
        for(CombinedHistoricalResultBefore before : befores){
            if(Objects.nonNull(before.getUcVoid()) && before.getUcVoid()){
                before.setUcGrade(null);
                before.setUcDate(null);
            }
            if(Objects.nonNull(before.getUeVoid()) && before.getUeVoid()){
                before.setUeGrade(null);
                before.setUeDate(null);
            }
            if(Objects.nonNull(before.getBlVoid()) && before.getBlVoid()){
                before.setBlGrade(null);
                before.setBlDate(null);
            }
            if(Objects.nonNull(before.getAtVoid()) && before.getAtVoid()){
                before.setAtGrade(null);
                before.setAtDate(null);
            }
        }
        HttpHeaders headers = new HttpHeaders();
        String zipFileName = "Enquiry Result_" + System.currentTimeMillis() + ".zip";
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFileName);
        byte[] zipBytes = new byte[0];
        try {
            zipBytes = FileUtil.createCsvZip(certInfos, befores);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating CSV");
        }
        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<List<CertInfoRandomDto>> getRamdomPdf(String examProfileSerialNo, String certStage, Integer allPassedCount, Integer partialFailedCount, Integer allFailedCount) {
        CertStage stage = null;

        try{
            stage = CertStage.valueOf(certStage);
        } catch(Exception e){
            throw new GenericException("cert.stage.not.found","Cert stage not found.");
        }
        List<CertInfo> randomAllPassed = certInfoRepository.getRandomCert(
                examProfileSerialNo,
                stage.name(),
                List.of("L1","L2","P"),
                List.of("L1","L2","P"),
                List.of("L1","L2","P"),
                List.of("L1","L2","P"),
                allPassedCount);
        List<CertInfo> randomPartialFailed = certInfoRepository.getPartialFailedCert(
                examProfileSerialNo,
                stage.name(),
                List.of("L1","L2","P"),
                List.of("F"),
                List.of("L1","L2","P"),
                List.of("F"),
                List.of("L1","L2","P"),
                List.of("F"),
                List.of("L1","L2","P"),
                List.of("F"),
                partialFailedCount);

        List<CertInfo> randomAllFailed = certInfoRepository.getRandomCert(
                examProfileSerialNo,
                stage.name(),
                List.of("F"),
                List.of("F"),
                List.of("F"),
                List.of("F"),
                allFailedCount);

        List<CertInfo> combinedCertInfoList = new ArrayList<>();
        combinedCertInfoList.addAll(randomAllPassed);   // Add all-passed certificates
        combinedCertInfoList.addAll(randomPartialFailed);  // Add partial-failed certificates
        combinedCertInfoList.addAll(randomAllFailed);   // Add all-failed certificates

        // Map CertInfo to CertInfoRandomDto
        List<CertInfoRandomDto> result = combinedCertInfoList.stream()
                .map(this::convertToRandomDto)
                .collect(Collectors.toList());

        // Return the combined list in a ResponseEntity
        return ResponseEntity.ok(result);
    }

    // Method to map CertInfo to CertInfoRandomDto
    private CertInfoRandomDto convertToRandomDto(CertInfo certInfo) {
        CertInfoRandomDto dto = new CertInfoRandomDto();
        dto.setId(certInfo.getId());
        dto.setCreatedDate(certInfo.getCreatedDate());
        dto.setModifiedDate(certInfo.getModifiedDate());
        dto.setModifiedBy(certInfo.getModifiedBy());
        dto.setCreatedBy(certInfo.getCreatedBy());
        dto.setRevokeDate(certInfo.getRevokeDate());
        dto.setExamProfileSerialNo(certInfo.getExamProfileSerialNo());
        dto.setExamDate(certInfo.getExamDate());
        dto.setName(certInfo.getName());
        dto.setHkid(certInfo.getHkid());
        dto.setPassportNo(certInfo.getPassportNo());
        dto.setEmail(certInfo.getEmail());
        dto.setBlnstGrade(certInfo.getBlnstGrade());
        dto.setUeGrade(certInfo.getUeGrade());
        dto.setUcGrade(certInfo.getUcGrade());
        dto.setAtGrade(certInfo.getAtGrade());
        dto.setPassed(certInfo.getPassed());
        dto.setRemark(certInfo.getRemark());
        dto.setCertStage(certInfo.getCertStage());
        dto.setCertStatus(certInfo.getCertStatus());
        dto.setOnHold(certInfo.getOnHold());
        dto.setValid(certInfo.getValid());
        dto.setOnHoldRemark(certInfo.getOnHoldRemark());
        dto.setLetterType(certInfo.getLetterType());
//        dto.setExamProfile(certInfo.getExamProfile());  // Assumes ExamProfileDto is used here appropriately
//        dto.setUrl(certInfo.getUrl());
        return dto;
    }

    @Override
    public void deleteFutureBatchEmail(String examProfileSerialNo) {
        LocalDateTime futureDate = LocalDate.now().plusDays(1).atTime(0, 0, 0);

        List<GcisBatchEmail> toBeDeleteGcisBatEmail =
                gcisBatchEmailRepository.findToBeDeleteBatchEmail(examProfileSerialNo, futureDate);

        certInfoRepository.updateNotYetSentCertBatchEmailToNull(examProfileSerialNo, futureDate);

        List<File> filesToDelete = toBeDeleteGcisBatEmail.stream()
                .map(GcisBatchEmail::getFile)
                .filter(Objects::nonNull)
                .peek(file -> {
                    String path = file.getPath();
                    if (path != null && !path.isEmpty()) {
                        minioUtil.deleteFile(path);
                    }
                })
                .collect(Collectors.toList());
        gcisBatchEmailRepository.deleteAll(toBeDeleteGcisBatEmail);
        fileRepository.deleteAll(filesToDelete);
    }

    @Override
    public byte[] downloadcert(String examProfileId, String certStage) throws IOException {
        CertStage stage = CertStage.valueOf(certStage);
        List<Long> ids = certInfoRepository.getAllByExamProfileId(examProfileId,stage);
        return getZippedPdfBinary(ids);
    }


    @Transactional
    public GcisBatchEmail createGcisBatchEmail(InsertGcisBatchEmailDto insertGcisBatchEmailDto, EmailTemplate notifyEmailTemplate, String processedXml, String listName, Long fileId){


        GcisBatchEmail gcisBatchEmail = new GcisBatchEmail();
        gcisBatchEmail.setEmailTemplateId(notifyEmailTemplate.getId());
        gcisBatchEmail.setXml(processedXml);
        gcisBatchEmail.setScheduleDatetime(insertGcisBatchEmailDto.getScheduledTime().atTime(9,0,0));
        gcisBatchEmail.setStatus("SCHEDULED");
        gcisBatchEmail.setGcisNotiListName(listName);
        gcisBatchEmail.setGcisTemplateName(listName);
        gcisBatchEmail.setFileId(fileId);
        gcisBatchEmailRepository.save(gcisBatchEmail);
        return gcisBatchEmail;
    }

    private List<List<CertInfo>> splitCertInfoList(List<CertInfo> inputList, Integer splitSize) {
        List<List<CertInfo>> outputList = new ArrayList<>();
        int size = inputList.size();

        for (int i = 0; i < size; i += splitSize) {
            outputList.add(inputList.subList(i, Math.min(size, i + splitSize)));
        }

        return outputList;
    }

    private CertEproof createCertEproofRecord(
            Long certInfoId,
            String uuid,
            Integer version,
            String token,
            String eWalletJson,
            String eCertHtml,
            String url,
            String keyName,
            String eproofId,
            String qrCodeString){



        CertEproof certEproof = new CertEproof();
        certEproof.setCertInfoId(certInfoId);
        certEproof.setEproofId(eproofId);
        certEproof.setKeyName(keyName);
        certEproof.setUuid(uuid);
        certEproof.setVersion(version);
        certEproof.setToken(token);
        certEproof.setEWalletJson(eWalletJson);
        certEproof.setECertHtml(eCertHtml);
        certEproof.setUrl(url);
        certEproof.setQrCodeString(qrCodeString);
        certEproofRepository.save(certEproof);

        return certEproof;
    }

    void deleteCertPdf(CertInfo certInfo){
         List<CertPdf> certPdfList = certPdfRepository.findByCertInfoId(certInfo.getId());
        if(certPdfList != null){
            certPdfRepository.deleteAll(certPdfList);
        }
    }

    @Transactional(noRollbackFor = Exception.class)
    void singleSignAndIssue(CertInfo certInfo){
        //TODO Trigger sign and issue action

        certInfo.setValid(true);
        certInfo.setCertStatus(CertStatus.SUCCESS);

        certInfoRepository.save(certInfo);
    }


    @Transactional
    public void createCertPdfRecord(CertInfo certInfo,File uploadedPdf){
        // Update CertPdf table record
        CertPdf certPdf = new CertPdf();
        certPdf.setCertInfoId(certInfo.getId());
        certPdf.setFileId(uploadedPdf.getId());
        certPdfRepository.save(certPdf);
    }

  /*  public void createCertRenewPdfRecord(CertInfo certInfo,File uploadedPdf){
        CertRenewPdf certPdf = new CertRenewPdf();
        certPdf.setCertInfoRenewId(certInfo.getId());
        certPdf.setFileId(uploadedPdf.getId());
        certRenewPdfRepository.save(certPdf);
    }*/

    @Transactional
    public void updateCertPdfRecord(CertInfo certInfo, File uploadedPdf){
//        CertPdf certPdf = certPdfRepository.findByCertInfoId(certInfo.getId());
//        if(certPdf != null){
//            certPdf.setFileId(uploadedPdf.getId());
//            certPdfRepository.save(certPdf);
//        }
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

        ExamProfile examProfile = examProfileRepository.findById(certInfo.getExamProfileSerialNo()).get();
        String processedCertOwnerName = getInitials(certInfo.getName().trim());
        String randomString = RandomStringUtils.random(4,true,true);
//        String processedCertOwnerName = certInfoRenew.getNewName().trim().replace(" ","_");
//        String currentTimeMillisString = String.valueOf(System.currentTimeMillis());
        String savePdfName = String.format("%s_%s_%s.pdf",
                examProfile.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_4)),
                processedCertOwnerName,
                randomString
        );
        return fileService.uploadFile(FILE_TYPE_CERT_RECORD,certRecordPath+"/"+certInfo.getExamProfileSerialNo(),savePdfName,new ByteArrayInputStream(mergedPdf));
    }
    public static String getInitials(String name) {
        StringBuilder initials = new StringBuilder();
        for (String part : name.split(" ")) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    public List<CertInfo> checkScv(String examProfileSerialNo,List<CertImportDto> csvData) throws Exception {
        Set<String> hkids = new HashSet<>();
        Set<String> passports = new HashSet<>();
        ExamProfile examProfile = examProfileRepository.getinfoByNo(examProfileSerialNo);
        LocalDate dbExamDate = examProfile.getExamDate();
        List<CertImportDto> certInfos = CertInfoMapper.INSTANCE.sourceToDestinationList(certInfoRepository.getInfoListByExamSerialNo(examProfileSerialNo));
        int count = certInfos.size();
        List<CertInfo> list = new ArrayList<>();
        certInfos.addAll(csvData);
        for(int i = 0; i < certInfos.size(); ++i){
            CertImportDto csv = certInfos.get(i);
            if(i < count ){
                hkids.add(csv.getHkid());
                passports.add(csv.getPassportNo());
            }else{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate examDate;
                int row = i+1-count;
                try{
                    examDate = LocalDate.parse(csv.getExamDate(), formatter);
                }catch (Exception e){
                    throw new ServiceException(ResultCode.CSV_EXAM_DATE,row);
                }
                if(!examDate.equals(dbExamDate)){
                    throw new ServiceException(ResultCode.CSV_EXAM_DATE,row);
                }

                if(csv.getHkid().contains("(") || csv.getHkid().contains(")")){
                    csv.setHkid(csv.getHkid().replaceAll("\\)","").replaceAll("\\(",""));
                }
                // Remove all white space
                csv.setHkid(csv.getHkid().replaceAll("\\s",""));

                if(csv.getHkid().isEmpty() && csv.getPassportNo().isEmpty()){
                    throw new ServiceException(ResultCode.HKID_PANNPORT_ABSENT,row);
                }
                if(!csv.getHkid().isEmpty() && !hkids.add(csv.getHkid())){
                    throw new ServiceException(ResultCode.HKID_EXITS,row);
                }
                if(!csv.getPassportNo().isEmpty() && !passports.add(csv.getPassportNo())){
                    throw new ServiceException(ResultCode.PASSPORT_EXITS,row);
                }
//                if(!csv.getLetterType().isEmpty() && (!csv.getLetterType().equals("Fail") && !csv.getLetterType().equals("Pass"))){
//                    throw new ServiceException(ResultCode.CSV_LETTER_TYPE,row);
//                }
                if(!CodeUtil.validEmail(csv.getEmail())){
                    throw new ServiceException(ResultCode.CSV_EMAIL_ERROR,row);
                }
                //组装batch info数据
                CertInfo certInfo = new CertInfo();
                certInfo.setExamProfileSerialNo(examProfileSerialNo);
                certInfo.setHkid(csv.getHkid());
                certInfo.setEncryptedHkid(EncryptionUtil.encrypt(csv.getHkid()));
                certInfo.setPassportNo(csv.getPassportNo());
                certInfo.setExamDate(examDate);
                certInfo.setName(csv.getName());
                certInfo.setEmail(csv.getEmail());
                certInfo.setUeGrade(csv.getUeGrade());
                certInfo.setUcGrade(csv.getUcGrade());
                certInfo.setAtGrade(csv.getAtGrade());
                certInfo.setBlnstGrade(csv.getBlnstGrade());
                certInfo.setLetterType(csv.getLetterType());
                certInfo.setCertStatus(CertStatus.SUCCESS);
                certInfo.setCertStage(CertStage.IMPORTED);
                certInfo.setOnHold(false);
                list.add(certInfo);
            }
        }
        return list;
    }

    @Override
    public byte [] getZippedPdfBinary(List<Long> certInfoIdList) throws IOException {
        List<Long> certInfoList = certInfoRepository.getByIdIn(certInfoIdList);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        List<File> latestPdfs = fileRepository.getLatestPdfForCerts2(certInfoList);
        /*List<File> latestPdfs = files.stream().collect(Collectors.groupingBy(File::getCertInfoId,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(File::getCreatedDate)),
                        Optional::get))).values().stream().collect(Collectors.toList());*/

        int currentIndex = 1;
        for(File file : latestPdfs){
            logger.info("Exporting cert into zip. File ID: {}, Current INDEX: {}, Total number: {}"
                    , file.getId()
                    , currentIndex
                    , certInfoList.size()
            );
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            zos.write(minioUtil.getFileAsByteArray(file.getPath()));
            zos.closeEntry();
            currentIndex++;
        }
       /* List<CertInfo> certInfoList = certInfoRepository.getByIdIn(certInfoIdList);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        int currentIndex = 1;
        for (CertInfo certInfo : certInfoList) {
            logger.info("Exporting cert into zip. Cert ID: {}, Current INDEX: {}, Total number: {}"
                    , certInfo.getId()
                    , currentIndex
                    , certInfoList.size()
            );
            if(certInfo.getPdfList() == null || certInfo.getPdfList().size()<=0){
                continue;
            }
            File latestPdf = fileRepository.getLatestPdfForCert(certInfo.getId());

            ZipEntry zipEntry = new ZipEntry(latestPdf.getName());
            zos.putNextEntry(zipEntry);
            zos.write(minioUtil.getFileAsByteArray(latestPdf.getPath()));
            zos.closeEntry();
        }*/
        zos.finish();
        zos.close();
        return baos.toByteArray();

    }

    @Override
    public byte [] previewCertPdf(Long certInfoId) throws IOException {
        CertInfo certInfo = certInfoRepository.findById(certInfoId).get();
        byte[] mergedPdf = null;
        try{
            byte[] atLeastOnePassedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_AT_LEAST_ONE_PASS);
            byte[] allFailedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE);
            InputStream appliedTemplate = "P".equals(certInfo.getLetterType())?new ByteArrayInputStream(atLeastOnePassedTemplate):new ByteArrayInputStream(allFailedTemplate);
            mergedPdf = documentGenerateService.getMergedDocument(appliedTemplate, DocumentOutputType.PDF,getMergeMapForCert(certInfo),getTableLoopMapForCert(certInfo));

            appliedTemplate.close();
            IOUtils.close(appliedTemplate);


        } catch (Exception e){
            certInfo.setCertStatus(CertStatus.FAILED);
            e.printStackTrace();
        }
        return mergedPdf;
    }

    public CertInfoRenew addCertInfoRenew(CertInfo info,UpdatePersonalDto personalDto){
        CertInfoRenew certInfoRenew = new CertInfoRenew();
        certInfoRenew.setCertInfoId(info.getId());
        certInfoRenew.setNewHkid(personalDto.getNewHkid());
        certInfoRenew.setOldHkid(info.getHkid());
        certInfoRenew.setNewPassport(personalDto.getNewPassport());
        certInfoRenew.setOldPassport(info.getPassportNo());
        certInfoRenew.setNewEmail(info.getEmail());
        certInfoRenew.setOldEmail(info.getEmail());
        certInfoRenew.setRemark(personalDto.getRemark());
//        certInfoRenew.setNewCname(personalDto.getNewName());
//        certInfoRenew.setOldCname(info.getCname());
        certInfoRenew.setNewName(personalDto.getNewName());
        certInfoRenew.setOldName(info.getName());
        certInfoRenew.setNewAtGrade(info.getAtGrade());
        certInfoRenew.setOldAtGrade(info.getAtGrade());
        certInfoRenew.setNewBlGrade(info.getBlnstGrade());
        certInfoRenew.setOldBlGrade(info.getBlnstGrade());
        certInfoRenew.setNewUcGrade(info.getUcGrade());
        certInfoRenew.setOldUcGrade(info.getUcGrade());
        certInfoRenew.setNewUeGrade(info.getUeGrade());
        certInfoRenew.setOldUeGrade(info.getUeGrade());
        certInfoRenew.setCertStage(info.getCertStage());
        certInfoRenew.setOldLetterType(info.getLetterType());
        certInfoRenew.setNewLetterType(info.getLetterType());
        certInfoRenew.setType(CertType.INFO_UPDATE);
        certInfoRenew.setCertStage(CertStage.GENERATED);
        certInfoRenew.setCertStatus(CertStatus.PENDING);
        certInfoRenew.setIsDelete(false);
        return certInfoRenew;
    }
}
