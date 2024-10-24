package com.hkgov.csb.eproof.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.ResultCode;
import com.hkgov.csb.eproof.dao.*;
import com.hkgov.csb.eproof.dto.*;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.entity.enums.CertType;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.exception.ServiceException;
import com.hkgov.csb.eproof.mapper.CertActionMapper;
import com.hkgov.csb.eproof.request.SendEmailRequest;
import com.hkgov.csb.eproof.security.EncryptionUtil;
import com.hkgov.csb.eproof.service.*;
import com.hkgov.csb.eproof.util.DocxUtil;
import com.hkgov.csb.eproof.util.EProof.EProofConfigProperties;
import com.hkgov.csb.eproof.util.EProof.EProofUtil;
import com.hkgov.csb.eproof.util.HKIDformatter;
import com.hkgov.csb.eproof.util.HttpUtils;
import com.hkgov.csb.eproof.util.MinioUtil;
import com.itextpdf.text.pdf.qrcode.WriterException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.hkgov.csb.eproof.util.HKIDformatter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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
    private final SystemParameterRepository systemParameterRepository;

    private final CertInfoRenewRepository certInfoRenewRepository;
    private final LetterTemplateService letterTemplateService;
    private final CertInfoService certInfoService;
    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;
    private final UserRepository userRepository;
    private final CertActionRepository certActionRepository;
    private final ActionTargetRepository actionTargetRepository;
    private final CertPdfRenewRepository certPdfRenewRepository;
    private final DocumentGenerateService documentGenerateService;
    private final DocxUtil docxUtil;
    private final FileService fileService;
    private final EProofConfigProperties eProofConfigProperties;

    private static final Gson GSON = new Gson();
    private final CertEproofRepository certEproofRepository;
    private final CertEproofRenewRepository certEproofRenewRepository;
    private final CertInfoRepository certInfoRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final GcisEmailServiceImpl gcisEmailServiceImpl;
    private final ExamProfileRepository examProfileRepository;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${document.qr-code.height}")
    private Integer qrCodeHeight;

    @Value("${document.qr-code.width}")
    private Integer qrCodeWidth;

    @Value("${document.qr-code.x}")
    private Integer qrCodeX;

    @Value("${document.qr-code.y}")
    private Integer qrCodeY;

    @Value("${minio.path.cert-renew-record}")
    private String certRenewRecordPath;
    private final CertPdfRepository certPdfRepository;

    @Override
    public void changeCertStatusToInProgress(Long certInfoId, CertStage certStage) {

    }


    private Map<DataFieldName, String> getMergeMapForRenewCert(CertInfoRenew certInfoRenew) throws JsonProcessingException {
        ExamProfile exam = certInfoRenew.getCertInfo().getExamProfile();

        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfoRenew,"cert");

        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        // Change the format of date for examMap
        examMap.put("examProfile.examDate",exam.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));
        examMap.put("examProfile.resultLetterDate",exam.getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));

        return docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap);
    }



    private Map<String,List> getTableLoopMapForCertRenew(CertInfoRenew certInfoRenew){
        List<ExamScoreDto> markDtoList = new ArrayList<>();
        if(StringUtils.isNotEmpty(certInfoRenew.getNewUeGrade())){
            markDtoList.add(new ExamScoreDto("Use of English (UE)",convertGradeToReadableGrade(certInfoRenew.getNewUeGrade())));
        }
        if(StringUtils.isNotEmpty(certInfoRenew.getNewUcGrade())){
            markDtoList.add(new ExamScoreDto("Use of Chinese (UC)",convertGradeToReadableGrade(certInfoRenew.getNewUcGrade())));
        }
        if(StringUtils.isNotEmpty(certInfoRenew.getNewAtGrade())){
            markDtoList.add(new ExamScoreDto("Aptitude Test (AT)",convertGradeToReadableGrade(certInfoRenew.getNewAtGrade())));
        }
        if(StringUtils.isNotEmpty(certInfoRenew.getNewBlGrade())) {
            markDtoList.add(new ExamScoreDto("BLNST", convertGradeToReadableGrade(certInfoRenew.getNewBlGrade())));
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

    @Override
    public void notifyCandidate(Long renewCertId, SendEmailRequest request) throws Exception {

        gcisEmailServiceImpl.sendTestEmail(request.getTo(), request.getTitle(), request.getHtmlBody());
        CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(renewCertId).get();
        certInfoRenew.setCertStatus(CertStatus.SUCCESS);
        certInfoRenewRepository.save(certInfoRenew);
    }

    @Override
    @Transactional
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
        ExamProfile examProfile = examProfileRepository.findById(certInfoRenew.getCertInfo().getExamProfileSerialNo()).get();
/*
        String processedCertOwnerName = getInitials(certInfoRenew.getNewName().trim());
*/
        String processedCertOwnerName = certInfoRenew.getName()
                .replace(" ","_")
                .replace(",","")
                .replace(".","");

        String randomString = RandomStringUtils.random(4,true,true);
//        String processedCertOwnerName = certInfoRenew.getNewName().trim().replace(" ","_");
//        String currentTimeMillisString = String.valueOf(System.currentTimeMillis());
        String savePdfName = String.format("%s_%s_%s.pdf",
                examProfile.getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_4)),
                processedCertOwnerName,
                randomString
        );
        return fileService.uploadFile(FILE_TYPE_CERT_RECORD_RENEW,certRenewRecordPath+"/"+certInfoRenew.getCertInfo().getExamProfileSerialNo(),savePdfName,new ByteArrayInputStream(mergedPdf));
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

    public void createCertRenewPdfRecord(CertInfoRenew certInfoRenew,File uploadedPdf){
        CertPdfRenew certPdf = new CertPdfRenew();
        certPdf.setCertInfoRenewId(certInfoRenew.getId());
        certPdf.setFileId(uploadedPdf.getId());
        certPdfRenewRepository.save(certPdf);
    }


    @Override
    public void removeCert(Long certInfoId) {
        CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(certInfoId).orElse(null);
        if(Objects.nonNull(certInfoRenew)){
            certInfoRenew.setIsDelete(true);
            certInfoRenewRepository.save(certInfoRenew);
        }
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
            File latestPdf = fileRepository. getLatestPdfForCertRenew(info.getId());
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
        certAction.setHkid(params.getHkid());
        certAction.setPassportNo(params.getPassportNo());
        certAction.setName(params.getName());
        certAction.setCanEmailAddress(params.getEmailTarget());
        certAction.setCanEmailContent(params.getEmailContent());
        certAction.setCanEmailSubject(params.getEmailSubject());
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

        EmailTemplate emailTemplate = emailTemplateRepository.findByName("Revoke_Request");
        gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
    }

    @Override
    public List<CertRevokeDto> getTodoRevoke() {
//        List<CertAction> certActions = certActionRepository.findAll();
        List<CertAction> certActions = certActionRepository.findPendingOrRejected();
        List<CertRevokeDto> certRevokeDtos = CertActionMapper.INSTANCE.sourceToDestination(certActions);
        for(int i = 0; i < certRevokeDtos.size(); i++){
            CertAction certAction = certActions.get(i);
            CertRevokeDto revokeDto = certRevokeDtos.get(i);
            revokeDto.setEmailTarget(certAction.getCanEmailAddress());
            revokeDto.setEmailContent(certAction.getCanEmailContent());
            revokeDto.setEmailSubject(certAction.getCanEmailSubject());
            if(Objects.nonNull(certAction.getUser())){
                revokeDto.setApprover(certAction.getUser().getName());
            }
        }
        return certRevokeDtos;
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
                    infoRenew.setCertStatus(CertStatus.PENDING);
                    break;
                }
                case GENERATED -> {
                    infoRenew.setCertStage(CertStage.SIGN_ISSUE);
                    infoRenew.setCertStatus(CertStatus.PENDING);
                    break;
                }
                case SIGN_ISSUE -> {
                    infoRenew.setCertStage(CertStage.NOTIFY);
                    infoRenew.setCertStatus(CertStatus.PENDING);
                    break;
                }
                case NOTIFY -> {
                    infoRenew.setCertStage(CertStage.COMPLETED);
                    infoRenew.setCertStatus(CertStatus.SUCCESS);
                    break;
                }
                default ->{
                    break;
                }
            }

        }
        certInfoRenewRepository.saveAll(list);

        switch (currentStage) {
            case RENEWED -> {
                EmailTemplate emailTemplate = emailTemplateRepository.findByName("Adhoc_Reissuance");
                gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
                break;
            }
            case GENERATED -> {
                EmailTemplate emailTemplate = emailTemplateRepository.findByName("Adhoc_Generate_Dispatch");
                gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
                break;
            }
            case SIGN_ISSUE -> {
                EmailTemplate emailTemplate = emailTemplateRepository.findByName("Adhoc_Sign_and_Issue_Dispatch");
                gcisEmailServiceImpl.sendTestEmail(emailTemplate.getIncludeEmails(), emailTemplate.getSubject(), emailTemplate.getBody());
                break;
            }
            default ->{
                break;
            }
        }
    }

    @Override
    public String prepareEproofUnsignJson(Long certInfoRenewId) throws NoSuchAlgorithmException {

        CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(certInfoRenewId).get();

        String issueToEn = certInfoRenew.getName();
        String issueToTc = certInfoRenew.getName();
        String issueToSc = certInfoRenew.getName();
        String eproofType = "personal";

        String en1, en2, en3,
                tc1, tc2, tc3,
                sc1, sc2, sc3;


        Map<String, String> extraInfo = new HashMap<>();
        extraInfo.put("cert_info_id", certInfoRenew.getId().toString());
        extraInfo.put("exam_profile_serial_no", certInfoRenew.getCertInfo().getExamProfileSerialNo());
        extraInfo.put("result_letter_date", certInfoRenew.getCertInfo().getExamProfile().getResultLetterDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));
        extraInfo.put("candidate_name", certInfoRenew.getNewName());
        extraInfo.put("exam_date", certInfoRenew.getCertInfo().getExamProfile().getExamDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN_3)));

        extraInfo.put("paper_1", StringUtils.isNotEmpty(certInfoRenew.getNewUeGrade()) ? "Use of English (UE)" : "");
        extraInfo.put("result_1", convertGradeToReadableGrade(certInfoRenew.getNewUeGrade()));

        extraInfo.put("paper_2", StringUtils.isNotEmpty(certInfoRenew.getNewUcGrade()) ? "Use of Chinese" : "");
        extraInfo.put("result_2", convertGradeToReadableGrade(certInfoRenew.getNewUcGrade()));

        extraInfo.put("paper_3", StringUtils.isNotEmpty(certInfoRenew.getNewAtGrade()) ? "Aptitude Test (AT)" : "");
        extraInfo.put("result_3", convertGradeToReadableGrade(certInfoRenew.getNewAtGrade()));

        extraInfo.put("paper_4", StringUtils.isNotEmpty(certInfoRenew.getNewBlGrade()) ? "BLNST" : "");
        extraInfo.put("result_4", convertGradeToReadableGrade(certInfoRenew.getNewBlGrade()));

        extraInfo.put("hkid_or_passport", certInfoRenew.getHkidOrPassport());

        LocalDateTime expiryDate = LocalDateTime.of(2099, 12, 31, 23, 59, 59);
        LocalDateTime issueDate = LocalDateTime.now();

//        String eproofId = certInfoRenew.getCertInfo().getEproofId();
        String eproofId = certInfoRenew.getEproofId(certInfoRenew.getCertInfo().getExamProfileSerialNo());

        String eproofTemplateCode;
        if ("P".equals(certInfoRenew.getNewLetterType())) {
            eproofTemplateCode = "CSBEPROOF";
        } else {
            eproofTemplateCode = "CSBEPROOFFAIL";
        }

        int majorVersion = 1;
        en1 = certInfoRenew.getName();
        en2 = certInfoRenew.getCertInfo().getExamDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        en3 = "Common Recruitment Examination / Basic Law and National Security Law Test (Degree / Professional Grades)";
        tc1 = certInfoRenew.getName();
        tc2 = certInfoRenew.getCertInfo().getExamDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        tc3 = "綜合招聘考試及《基本法及香港國安法》測試（學位/專業程度職系）";
        sc1 = certInfoRenew.getName();
        sc2 = certInfoRenew.getCertInfo().getExamDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
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
                certInfoRenew.getNewHkid()
        );

    }

    @Override
    public byte[] prepareEproofPdf(Long certInfoRenewId, PrepareEproofPdfRequest prepareEproofPdfRequest) throws Exception {

        CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(certInfoRenewId).get();
        //Register the json to get the UUID from EProof
        String uuid = "";
        if(CertType.RESULT_UPDATE.equals(certInfoRenew.getType())){
            // set uuid to null to generate a new uuid for the new document
            uuid = null;
        } else{
            // use original uuid to update the version number
            uuid = certInfoRenew.getCertInfo().getCertEproof().getUuid();
        }
        String publicKey = prepareEproofPdfRequest.getPublicKey();
        logger.debug(publicKey);
        String keyName = systemParameterRepository.findByName(publicKey).orElseThrow(() -> new GenericException("public.key.not.found","Public key not found.")).getValue();
        String eproofTypeId = null;

        if("P".equals(certInfoRenew.getNewLetterType())){
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
                certInfoRenew.getCertInfo().getExamProfile().getEffectiveDate().atTime(0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+08:00")),
                certInfoRenew.getNewHkid()
        );

        logger.info("[registerResult]" + GSON.toJson(registerResult));


        uuid = (String) registerResult.get("uuid");
        Integer returnVersion = (Integer) registerResult.get("version");
        String token = (String) registerResult.get("token");


        logger.info("[KeyName]" + keyName);
        logger.info("[uuid]" + uuid);
        logger.info("[returnVersion]" + returnVersion);


        // Get QR code string from eProof
        String qrCodeString = EProofUtil.getQrCodeString(
                (String) registerResult.get("eProofJson"),
                uuid,
                returnVersion,
                null,
                -1
        );

        logger.debug("[qrCodeString]" + qrCodeString);



        createCertEproofRecord(
                certInfoRenewId,
                uuid,
                returnVersion,
                token,
                (String)registerResult.get("eProofJson"),
                "",
                eProofConfigProperties.getDownloadUrlPrefix()+ URLEncoder.encode(token, StandardCharsets.UTF_8),
                keyName,
//                certInfoRenew.getCertInfo().getEproofId(),
                certInfoRenew.getEproofId(certInfoRenew.getCertInfo().getExamProfileSerialNo()),
                qrCodeString
        );
        /*if(certEproof != null){
            //TODO Signed cert and sign again
            logger.error("Found existing cert_eproof record. Update cert_eproof record. Cert name: "+certInfo.getName());

            updateCertEproofRenewRecord(
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
                    certInfoRenewId,
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
        }*/

        // Update the PDF
        File latestCert = fileRepository.getLatestPdfForCertRenew(certInfoRenewId);
        InputStream is = minioUtil.getFileAsStream(latestCert.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PDDocument pdDocument = PDDocument.load(is)) {
            // Retrieve the document information
            PDDocumentInformation info = pdDocument.getDocumentInformation();
            // Set the title and author
            String pdfTitle = "";

            if(certInfoRenew.getNewLetterType() != null && "P".equals(certInfoRenew.getNewLetterType())){
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

            logger.info("PDF Title: " + info.getTitle());
            logger.info("PDF Author: " + info.getAuthor());
            logger.info("PDF Keywords: " + info.getKeywords());

            pdDocument.save(baos);
        } catch (Exception e){
            e.printStackTrace();
        }
        baos.close();

//        FileOutputStream fos = new FileOutputStream("C:\\Users\\IGS\\Downloads\\test20240827\\test.pdf");
//        fos.write(baos.toByteArray());
//        fos.close();

        // Upload the updated PDF
        minioUtil.uploadFile(latestCert.getPath(), baos);

        // return the binary array
        return baos.toByteArray();
        //Completed preparing for Eproof PDF
    }

    @Override
    @Transactional
    public void uploadSignedPdf(Long certInfoRenewId, MultipartFile file) {
        {
            CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(certInfoRenewId).orElse(null);
            CertInfo certInfo = certInfoRenew.getCertInfo();
            if(Objects.isNull(certInfoRenew)){
                throw new GenericException(ExceptionEnums.CERT_NOT_EXIST);
            }
            if(!CertStage.SIGN_ISSUE.equals(certInfoRenew.getCertStage()) && !CertStatus.IN_PROGRESS.equals(certInfoRenew.getCertStatus())){
                throw new GenericException(ExceptionEnums.CERT_INFO_NOT_UPDATE);
            }

            try{

                certInfo.setActualSignTime(LocalDateTime.now());
                certInfoRepository.save(certInfo);

//            File uploadedPdf = fileService.uploadFile(FILE_TYPE_CERT_RECORD,certRecordPath,file.getName(),file.getInputStream());
                deleteCertPdf(certInfoRenew);
                File uploadedPdf = this.uploadCertPdf(certInfoRenew, file.getBytes());
                this.createCertPdfRecord(certInfoRenew,uploadedPdf);

            }catch (IOException e){
                throw new GenericException(ExceptionEnums.CANNOT_UPLOAD_SIGNED_PDF_FOR_CERT);
            }
        }
    }

    @Transactional
    public void createCertPdfRecord(CertInfoRenew certInfoRenew,File uploadedPdf){
        // Update CertPdf table record
        CertPdfRenew certPdfRenew = new CertPdfRenew();
        certPdfRenew.setCertInfoRenewId(certInfoRenew.getId());
        certPdfRenew.setFileId(uploadedPdf.getId());
        certPdfRenewRepository.save(certPdfRenew);
    }
    void deleteCertPdf(CertInfoRenew certInfoRenew){
        List<CertPdfRenew> certPdfRenew = certPdfRenewRepository.findByCertInfoRenewId(certInfoRenew.getId());
        if(certPdfRenew != null){
            certPdfRenewRepository.deleteAll(certPdfRenew);
        }
    }

    @Override
    @Transactional
    public void issueCert(Long certInfoRenewId) throws Exception {

        CertEproofRenew certEproofRenew = certEproofRenewRepository.findByCertInfoRenewId(certInfoRenewId);

        CertInfoRenew certInfoRenew = certInfoRenewRepository.findById(certInfoRenewId).get();

        File certPdf = fileRepository.getLatestPdfForCertRenew(certInfoRenewId);
//        File certPdf = certInfoRenew.getPdfList()!=null&&certInfoRenew.getPdfList().size()>0?certInfoRenew.getPdfList().get(0):null;
        logger.info("Pdf name: "+certPdf.getName());

        byte[] certPdfBinary = minioUtil.getFileAsByteArray(certPdf.getPath());

        EProofUtil.issuePdf(certEproofRenew.getUuid(),EProofUtil.calcPdfHash(certPdfBinary));


        if (CertType.RESULT_UPDATE.equals(certInfoRenew.getType())){
            certInfoService.actualRevokeWithEproofModule(certInfoRenew.getCertInfo().getId(),"Update result. cert_info_renew_id = "+certInfoRenew.getId());

            CertInfo newCertInfo = certInfoRenew.getCertInfo().clone();
            newCertInfo.setId(null);
            newCertInfo.setCertEproof(null);
            newCertInfo.setPdfList(null);
            newCertInfo.setCertInfoRenewList(null);
            this.replaceCertInfoWithCertInfoRenew(newCertInfo,certInfoRenew);

            CertEproof newCertEproof = new CertEproof();
            newCertEproof.setCertInfoId(newCertInfo.getId());
            this.replaceCertEproofWithCertEproofRenew(newCertEproof,certEproofRenew);

            CertPdf oldPdf = certPdfRepository.getLatestCertPdf(certInfoRenew.getCertInfo().getId());
            CertPdf newCertPdf =oldPdf.clone();
            newCertPdf.setCertInfoId(newCertInfo.getId());
            this.replaceCertPdfWithCertPdfRenew(newCertPdf,certPdfRenewRepository.getLatestCertPdf(certInfoRenewId));


        }else{
            CertInfo certInfo = certInfoRenew.getCertInfo();
            this.replaceCertInfoWithCertInfoRenew(certInfo,certInfoRenew);
            this.replaceCertEproofWithCertEproofRenew(certInfo.getCertEproof(),certEproofRenew);
            CertPdf oldPdf = certPdfRepository.getLatestCertPdf(certInfo.getId());
            CertPdfRenew newPdf = certPdfRenewRepository.getLatestCertPdf(certInfoRenewId);
            this.replaceCertPdfWithCertPdfRenew(oldPdf,newPdf);
        }

        certInfoRenew.setCertStage(CertStage.SIGN_ISSUE);
        certInfoRenew.setCertStatus(CertStatus.SUCCESS);
        certInfoRenewRepository.save(certInfoRenew);

    }

    @Override
    public HavePendingCaseDto havePendingCase(HavePendingCaseDto requestDto) {
        HavePendingCaseDto dto = new HavePendingCaseDto();
        boolean havePendingCase = false;
        if(StringUtils.isBlank(requestDto.getHkid()) && StringUtils.isBlank(requestDto.getPassport())){
            dto.setHavePendingCase(havePendingCase);
            return dto;
        }
        String hkid = requestDto.getHkid();
        String passport = requestDto.getPassport();
        List<CertInfoRenew> certInfoRenews =  StringUtils.isNotBlank(hkid) ? certInfoRenewRepository.getInfoByHkid(hkid)
                : certInfoRenewRepository.getInfoByPassport(passport);
        List<CertAction> certActions = StringUtils.isNotBlank(hkid) ? certActionRepository.getinfoByHkid(hkid)
                : certActionRepository.getinfoByPassport(passport);
        havePendingCase = CollUtil.isNotEmpty(certInfoRenews) | CollUtil.isNotEmpty(certActions);
        dto.setHavePendingCase(havePendingCase);
        return dto;
    }

    private void replaceCertPdfWithCertPdfRenew(CertPdf oldPdf, CertPdfRenew newPdf) {
        oldPdf.setFileId(newPdf.getFileId());
        certPdfRepository.save(oldPdf);
    }

    private void replaceCertEproofWithCertEproofRenew(CertEproof certEproof, CertEproofRenew certEproofRenew) {
        certEproof.setEproofId(certEproofRenew.getEproofId());
        certEproof.setKeyName(certEproofRenew.getKeyName());
        certEproof.setUuid(certEproofRenew.getUuid());
        certEproof.setVersion(certEproofRenew.getVersion());
        certEproof.setECertHtml(certEproofRenew.getECertHtml());
        certEproof.setEWalletJson(certEproofRenew.getEWalletJson());
        certEproof.setToken(certEproofRenew.getToken());
        certEproof.setUrl(certEproofRenew.getUrl());
        certEproof.setQrCodeString(certEproofRenew.getQrCodeString());
        certEproofRepository.save(certEproof);
    }

    private void replaceCertInfoWithCertInfoRenew(CertInfo certInfo, CertInfoRenew certInfoRenew) throws Exception {
        certInfo.setHkid(certInfoRenew.getNewHkid());
        certInfo.setEncryptedHkid(EncryptionUtil.encrypt(certInfoRenew.getNewHkid()));
        certInfo.setPassportNo(certInfoRenew.getNewPassport());
        certInfo.setName(certInfoRenew.getNewName());
        certInfo.setEmail(certInfoRenew.getNewEmail());
        certInfo.setAtGrade(certInfoRenew.getNewAtGrade());
        certInfo.setBlnstGrade(certInfoRenew.getNewBlGrade());
        certInfo.setUcGrade(certInfoRenew.getNewUcGrade());
        certInfo.setUeGrade(certInfoRenew.getNewUeGrade());
        certInfo.setValid(true);
        certInfoRepository.save(certInfo);
    }

    private CertEproofRenew createCertEproofRecord(
            Long certInfoRenewId,
            String uuid,
            Integer version,
            String token,
            String eWalletJson,
            String eCertHtml,
            String url,
            String keyName,
            String eproofId,
            String qrCodeString){



        CertEproofRenew certEproof = new CertEproofRenew();
        certEproof.setCertInfoRenewId(certInfoRenewId);
        certEproof.setEproofId(eproofId);
        certEproof.setKeyName(keyName);
        certEproof.setUuid(uuid);
        certEproof.setVersion(version);
        certEproof.setToken(token);
        certEproof.setEWalletJson(eWalletJson);
        certEproof.setECertHtml(eCertHtml);
        certEproof.setUrl(url);
        certEproof.setQrCodeString(qrCodeString);
        certEproofRenewRepository.save(certEproof);

        return certEproof;
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
}
