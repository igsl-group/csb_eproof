package com.hkgov.csb.eproof.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.dao.*;
import com.hkgov.csb.eproof.dto.*;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.entity.enums.CertType;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertInfoMapper;
import com.hkgov.csb.eproof.service.*;
import com.hkgov.csb.eproof.util.CodeUtil;
import com.hkgov.csb.eproof.util.DocxUtil;
import com.hkgov.csb.eproof.util.EProof.EProofConfigProperties;
import com.hkgov.csb.eproof.util.EProof.EProofUtil;
import com.hkgov.csb.eproof.util.EProof.FileUtil;
import com.hkgov.csb.eproof.util.MinioUtil;
import com.itextpdf.text.pdf.qrcode.WriterException;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.docx4j.model.fields.merge.DataFieldName;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    @Value("${minio.path.cert-record}")
    private String certRecordPath;

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

    private static final Gson GSON = new Gson();

    @Value("${document.qr-code.height}")
    private Integer qrCodeHeight;

    @Value("${document.qr-code.width}")
    private Integer qrCodeWidth;

    @Value("${document.qr-code.x}")
    private Integer qrCodeX;

    @Value("${document.qr-code.y}")
    private Integer qrCodeY;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CertPdfRepository certPdfRepository;

    @Override
    @Transactional
    public Page<CertInfo> search(CertSearchDto request, List<String> certStageList, List<String> certStatusList, Pageable pageable) {
        return certInfoRepository.certSearch(request,certStageList,certStatusList,pageable);
    }

    @Override
    public Boolean batchImport(String examProfileSerialNo, List<CertImportDto> csvData) {
        List<CertInfo> certInfos = checkScv(examProfileSerialNo,csvData);
        return certInfoRepository.saveAll(certInfos).size() == csvData.size();
    }


    @Override
    public Boolean dispatch(String examProfileSerialNo, CertStage currentStage) {
        if(!currentStage.equals(CertStage.IMPORTED) && !currentStage.equals(CertStage.GENERATED)
                && !currentStage.equals(CertStage.SIGN_ISSUE) && !currentStage.equals(CertStage.NOTIFY)){
            throw new ServiceException(ResultCode.STAGE_ERROR);
        }
        List<CertInfo> list = certInfoRepository.getinfoByNoAndStatus(examProfileSerialNo,currentStage);
        if(list.isEmpty()){
            throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
        }
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
            certInfo.setCertStatus(CertStatus.PENDING);
        }
        certInfoRepository.saveAll(list);
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
    public List<CertInfo>  batchScheduleCertSignAndIssue(String examProfileSerialNo) {

        List<CertInfo> alreadyScheduledCert = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.SIGN_ISSUE,List.of(CertStatus.SCHEDULED));
        if (!alreadyScheduledCert.isEmpty()){
            return null;
        }

        List<CertInfo> inProgressCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.SIGN_ISSUE,List.of(CertStatus.IN_PROGRESS));
        List<CertInfo> pendingSignAndIssueCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.SIGN_ISSUE,List.of(CertStatus.PENDING));

        List<CertInfo> toBeScheduledCert = new ArrayList<>();
        toBeScheduledCert.addAll(inProgressCertList);
        toBeScheduledCert.addAll(pendingSignAndIssueCertList);

        toBeScheduledCert.forEach(cert->{
            cert.setCertStatus(CertStatus.SCHEDULED);
        });

        certInfoRepository.saveAll(toBeScheduledCert);

        // Set access token to null to force the following action to get a new access token
        eProofConfigProperties.setAccessToken(null);

        return pendingSignAndIssueCertList;
    }

    @Override
//    @Transactional(noRollbackFor = Exception.class)

    public void batchGeneratePdf(String examProfileSerialNo) throws Exception {

        List<CertInfo> inProgressCertList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.GENERATED,List.of(CertStatus.IN_PROGRESS));
        byte[] passTemplateInputStream = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_AT_LEAST_ONE_PASS);
        byte[] allFailedTemplate = letterTemplateService.getTemplateByNameAsByteArray(LETTER_TEMPLATE_ALL_FAILED_TEMPLATE);
//        try{
        for (CertInfo cert : inProgressCertList) {
            pdfGenerateService.singleGeneratePdf(cert,passTemplateInputStream,allFailedTemplate,true,false);
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
        examMap.put("examProfile.examDate",exam.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_2)));
        examMap.put("examProfile.resultLetterDate",exam.getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_2)));

        return docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap);
    }

    private Map<String,List> getTableLoopMapForCert(CertInfo certInfo){
        List<ExamScoreDto> markDtoList = new ArrayList<>();

        if(StringUtils.isNotEmpty(certInfo.getUcGrade())){
            markDtoList.add(new ExamScoreDto("Use of Chinese",convertGradeToReadableGrade(certInfo.getUcGrade())));
        }
        if(StringUtils.isNotEmpty(certInfo.getUeGrade())){
            markDtoList.add(new ExamScoreDto("Use of English",convertGradeToReadableGrade(certInfo.getUeGrade())));
        }
        if(StringUtils.isNotEmpty(certInfo.getAtGrade())){
            markDtoList.add(new ExamScoreDto("Aptitude Test",convertGradeToReadableGrade(certInfo.getAtGrade())));
        }
        if(StringUtils.isNotEmpty(certInfo.getBlnstGrade())) {
            markDtoList.add(new ExamScoreDto("Basic Law and National Security Law Test", convertGradeToReadableGrade(certInfo.getBlnstGrade())));
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

        File certPdf = certInfo.getPdfList()!=null&&certInfo.getPdfList().size()>0?certInfo.getPdfList().get(0):null;

        byte[] certPdfBinary = minioUtil.getFileAsByteArray(certPdf.getPath());


        EProofUtil.issuePdf(certEproof.getUuid(),EProofUtil.calcPdfHash(certPdfBinary));

        certInfo.setCertStage(CertStage.SIGN_ISSUE);
        certInfo.setCertStatus(CertStatus.SUCCESS);

        certInfoRepository.save(certInfo);
    }

    @Override
    public String prepareEproofUnsignJson(Long certInfoId) {
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
        extraInfo.put("result_letter_date", certInfo.getExamProfile().getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_2)));
        extraInfo.put("candidate_name", certInfo.getName());
        extraInfo.put("exam_date", certInfo.getExamProfile().getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_2)));

        extraInfo.put("paper_1", StringUtils.isNotEmpty(certInfo.getUcGrade())? "Use of Chinese" : "");
        extraInfo.put("result_1", certInfo.getUcGrade());

        extraInfo.put("paper_2", StringUtils.isNotEmpty(certInfo.getUeGrade()) ? "Use of English" : "");
        extraInfo.put("result_2", certInfo.getUeGrade());

        extraInfo.put("paper_3", StringUtils.isNotEmpty(certInfo.getAtGrade()) ? "Aptitude Test" : "");
        extraInfo.put("result_3", certInfo.getAtGrade());

        extraInfo.put("paper_4", StringUtils.isNotEmpty(certInfo.getBlnstGrade())? "Basic Law and National Security Law Test" : "");
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
        en2 = certInfo.getHkidOrPassport();
        en3 = issueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        tc1 = certInfo.getName();
        tc2 = certInfo.getHkidOrPassport();
        tc3 = issueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        sc1 = certInfo.getName();
        sc2 = certInfo.getHkidOrPassport();
        sc3 = issueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
                EProofUtil.type.personal
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
                downloadExpiryDateTime
        );

        logger.debug("[registerResult]" + GSON.toJson(registerResult));


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
            logger.error("Found existing cert_eproof record. Update cert_eproof record. Cert name: "+certInfo.getName());

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

    private byte[] generateQrCodeBinary(String qrCodeString) throws WriterException, com.google.zxing.WriterException, IOException {
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
    public void insertGcisBatchEmail(String examProfileSerialNo, InsertGcisBatchEmailDto insertGcisBatchEmailDto) {
        List<CertInfo> certInfoList = certInfoRepository.getCertByExamSerialAndStageAndStatus(examProfileSerialNo,CertStage.NOTIFY,List.of(CertStatus.SCHEDULED));
        List<List<CertInfo>> choppedCertInfo2dList = splitCertInfoList(certInfoList, 1000);

        EmailTemplate notifyEmailTemplate = emailTemplateRepository.findByName(Constants.EMAIL_TEMPLATE_NOTIFY);
        for (List<CertInfo> choppedCertInfoList : choppedCertInfo2dList) {
            GcisBatchEmail gcisBatchEmail = this.createGcisBatchEmail(insertGcisBatchEmailDto,notifyEmailTemplate,choppedCertInfoList);
            choppedCertInfoList.forEach(certInfo -> {
                certInfo.setGcisBatchEmailId(gcisBatchEmail.getId());
            });
            certInfoRepository.saveAll(choppedCertInfoList);
        }
    }

    @Override

    public void approveRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception {
        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));
        List<CertInfo> toBeRevokeCertInfoList = certAction.getCertInfos();

        for (CertInfo certInfo : toBeRevokeCertInfoList) {
            this.actualRevokeWithEproofModule(certInfo.getId(), certApproveRejectRevokeDto.getRemark());
        }
        certAction.setRemark(certApproveRejectRevokeDto.getRemark());
        certAction.setCanEmailAddress(certApproveRejectRevokeDto.getEmailTarget());
        certAction.setCanEmailContent(certApproveRejectRevokeDto.getEmailContent());
        certAction.setStatus(CertStatus.APPROVED);
        certActionRepository.save(certAction);
        //TODO Send email
    }

    public void rejectRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception {
        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));

        certAction.setRemark(certApproveRejectRevokeDto.getRemark());
        certAction.setCanEmailAddress(certApproveRejectRevokeDto.getEmailTarget());
        certAction.setCanEmailContent(certApproveRejectRevokeDto.getEmailContent());
        certAction.setStatus(CertStatus.REJECTED);
        certActionRepository.save(certAction);
    }

    public void resubmitRevoke(Long certActionId, CertApproveRejectRevokeDto certApproveRejectRevokeDto) throws Exception {
        CertAction certAction = certActionRepository.findById(certActionId).orElseThrow(()->new GenericException("cert.action.not.found","Cert action not found."));

        certAction.setRemark(certApproveRejectRevokeDto.getRemark());
        certAction.setCanEmailAddress(certApproveRejectRevokeDto.getEmailTarget());
        certAction.setCanEmailContent(certApproveRejectRevokeDto.getEmailContent());
        certAction.setStatus(CertStatus.PENDING);
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


    @Transactional
    public GcisBatchEmail createGcisBatchEmail(InsertGcisBatchEmailDto insertGcisBatchEmailDto, EmailTemplate notifyEmailTemplate, List<CertInfo> choppedCertInfoList){
        GcisBatchEmail gcisBatchEmail = new GcisBatchEmail();
        gcisBatchEmail.setEmailTemplateId(notifyEmailTemplate.getId());
        gcisBatchEmail.setXml("");
        gcisBatchEmail.setScheduleDatetime(insertGcisBatchEmailDto.getScheduledTime().atTime(9,0,0));
        gcisBatchEmail.setStatus("SCHEDULED");
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
        CertPdf certPdf = certPdfRepository.findByCertInfoId(certInfo.getId());
        if(certPdf != null){
            certPdfRepository.delete(certPdf);
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
        CertPdf certPdf = certPdfRepository.findByCertInfoId(certInfo.getId());
        if(certPdf != null){
            certPdf.setFileId(uploadedPdf.getId());
            certPdfRepository.save(certPdf);
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

    public List<CertInfo> checkScv(String examProfileSerialNo,List<CertImportDto> csvData){
        Set<String> hkids = new HashSet<>();
        Set<String> passports = new HashSet<>();
        ExamProfile examProfile = examProfileRepository.getinfoByNo(examProfileSerialNo);
        LocalDate dbExamDate = examProfile.getExamDate();
        List<CertImportDto> certInfos = CertInfoMapper.INSTANCE.sourceToDestinationList(certInfoRepository.getInfoListByExamSerialNo(examProfileSerialNo));
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
                if(!codeUtil.validEmai(csv.getEmail())){
                    throw new ServiceException(ResultCode.CSV_EMAIL_ERROR,row);
                }
                //组装batch info数据
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
        List<CertInfo> certInfoList = certInfoRepository.getByIdIn(certInfoIdList);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (CertInfo certInfo : certInfoList) {
            if(certInfo.getPdfList() == null || certInfo.getPdfList().size()<=0){
                continue;
            }
            File latestPdf = fileRepository. getLatestPdfForCert(certInfo.getId());

            ZipEntry zipEntry = new ZipEntry(latestPdf.getName());
            zos.putNextEntry(zipEntry);
            zos.write(minioUtil.getFileAsByteArray(latestPdf.getPath()));
            zos.closeEntry();
        }
        zos.finish();
        zos.close();
        return baos.toByteArray();

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
