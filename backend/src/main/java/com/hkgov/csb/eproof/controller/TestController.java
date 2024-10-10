package com.hkgov.csb.eproof.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dao.EmailTemplateRepository;
import com.hkgov.csb.eproof.dao.GcisBatchEmailRepository;
import com.hkgov.csb.eproof.dto.ExamScoreDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.EmailTemplate;
import com.hkgov.csb.eproof.entity.ExamProfile;
import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.request.ManualResendBatchEmailRequest;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.DocumentGenerateService;
import com.hkgov.csb.eproof.service.GcisBatchEmailService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.service.impl.CertInfoServiceImpl;
import com.hkgov.csb.eproof.util.DocxUtil;
import com.hkgov.csb.eproof.util.EProof.EProofUtil;
import com.hkgov.csb.eproof.util.EmailUtil;
import com.hkgov.csb.eproof.util.MinioUtil;
import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import jakarta.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Transactional(rollbackFor = Exception.class)
public class TestController {
    @Resource
    private PermissionService permissionService;
    @Resource
    private CertInfoService certInfoService;
    @Resource
    private CertInfoServiceImpl certInfoServiceImpl;
    @Autowired
    private CertInfoRepository certInfoRepository;

    @Autowired
    private DocxUtil docxUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GcisBatchEmailService gcisBatchEmailService;

    @Autowired
    private GcisBatchEmailRepository gcisBatchEmailRepository;
    @Value("${document.qr-code.height}")
    private Integer qrCodeHeight;

    @Value("${document.qr-code.width}")
    private Integer qrCodeWidth;

    @Value("${document.qr-code.x}")
    private Integer qrCodeX;

    @Value("${document.qr-code.y}")
    private Integer qrCodeY;

    @Autowired
    private DocumentGenerateService documentGenerateService;
    @Value("${document.generate-temp-source}")
    private String tmpSource;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public TestController(EmailTemplateRepository emailTemplateRepository, EmailUtil emailUtil) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailUtil = emailUtil;
    }

    @GetMapping("/fillDocument")
    public ResponseEntity fillDocument() throws Exception {
        CertInfo certInfo = certInfoRepository.findById(1L).get();
        ExamProfile exam = certInfo.getExamProfile();

        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        logger.info("map content: {}",certInfoMap);
        logger.info("json: {}",objectMapper.writeValueAsString(certInfoMap));

        FileInputStream inputStream = new FileInputStream("C:\\Users\\IGS\\Documents\\CSB_EProof\\Cert sample\\Result letter templates\\test_template.docx");
        byte[] mergedDocx = docxUtil.getMergedDocumentBinary(inputStream,docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap));
//        byte[] convertedPdf = docxUtil.convertDocxToPdf(new ByteArrayInputStream(mergedDocx));
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test.pdf")
                .build()
        );


        return ResponseEntity.ok().headers(header).body(mergedDocx);
    }

    @GetMapping("/fillDocument2")
    public ResponseEntity<byte[]> fillDocument2() throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test.pdf")
                .build()
        );
//        CertInfo certInfo = certInfoRepository.findById(442L).get();
//        ExamProfile exam = certInfo.getExamProfile();
//
//        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
//        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        FileInputStream inputStream = new FileInputStream(tmpSource);
        byte[] pdfBytes = docxUtil.convertDocxToPdf_POI(inputStream);
        return ResponseEntity.ok().headers(header).body(pdfBytes);
    }

    @GetMapping("/generateDocumentWithQrCodeDemo")
    public ResponseEntity generateDocumentWithQrCodeDemo(
            @RequestParam(defaultValue = "") Long certId,
            @RequestParam(defaultValue = "") Integer qrx,
            @RequestParam(defaultValue = "") Integer qry,
            @RequestParam(defaultValue = "") Integer qrw,
            @RequestParam(defaultValue = "") Integer qrh
    ) throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test.pdf")
                .build()
        );

        byte [] previewCertPdf = certInfoService.previewCertPdf(certId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PDDocument pdDocument = PDDocument.load(previewCertPdf)) {
            byte [] qrCodeImageBinary = certInfoServiceImpl.generateQrCodeBinary("{\"data\":{\"initVector\":\"ndlftcAV96tINBUSnjirBA==\",\"jwt\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjI1MzQwMjMwMDc5OSwidHlwZSI6IjA3IiwiaWF0IjoxNzI1MjI4NzgwLCJzdWIiOiJkMWI4MjgxMS03YjRlLTQ5Y2QtOWRlZi03YmRlZWRmNGQyNzQifQ.WB3nf6gy9eeRgLwqrI59d_a86VaBXcES-pem9P_BtDc\",\"key\":\"JCxY2GfePT4wqC5J6rFez+J1QkQBHZySgmluVVgs8GE=\",\"shared_eproof_uuid\":\"d1b82811-7b4e-49cd-9def-7bdeedf4d274\"},\"type_id\":\"2c\"}");

            Integer x = qrx != null ? qrx : qrCodeX;
            Integer y = qry != null ? qry : qrCodeY;
            Integer w = qrw != null ? qrw : qrCodeWidth;
            Integer h = qrh != null ? qrh : qrCodeHeight;

            PDImageXObject qrCodeImage = PDImageXObject.createFromByteArray(pdDocument, qrCodeImageBinary, "QR Code");
            try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdDocument.getPage(0), PDPageContentStream.AppendMode.APPEND, true)) {
                contentStream.drawImage(qrCodeImage, x, y, w, h);
            }

            pdDocument.save(baos);
        }
        baos.close();

        return ResponseEntity.ok().headers(header).body(baos.toByteArray());
//        return ResponseEntity.ok().headers(header).body(previewCertPdf);
    }


    @GetMapping("/generateDocumentDemo")
    public ResponseEntity generateDocumentDemo() throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test.pdf")
                .build()
        );
        CertInfo certInfo = certInfoRepository.findById(1L).get();
        ExamProfile exam = certInfo.getExamProfile();

        List<ExamScoreDto> markDtoList = new ArrayList<>();
        if(certInfo.getUcGrade() != null){
            markDtoList.add(new ExamScoreDto("Use of Chinese (UC)",certInfo.getUcGrade()));
        }
        if(certInfo.getUeGrade() != null){
            markDtoList.add(new ExamScoreDto("Use of English (UE)",certInfo.getUeGrade()));
        }

        if(certInfo.getAtGrade() != null){
            markDtoList.add(new ExamScoreDto("Aptitude Test (AT)",certInfo.getAtGrade()));
        }
        if(certInfo.getBlnstGrade() != null){
            markDtoList.add(new ExamScoreDto("BLNST", certInfo.getBlnstGrade()));
        }

        if (markDtoList.size() < 4){
            for(int i = markDtoList.size(); i <= 4; i++){
                markDtoList.add(new ExamScoreDto(" "," "));
            }
        }


        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");


        HashMap<String,List> map = new HashMap<>();
        map.put("examResults",markDtoList);

        FileInputStream inputStream = new FileInputStream(tmpSource);

        byte [] mergedDocument = documentGenerateService.getMergedDocument(inputStream, DocumentOutputType.PDF,docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap),map);
//        byte[] pdfBytes = docxUtil.convertDocxToPdf_POI(inputStream);
//        return ResponseEntity.ok().headers(header).body(pdfBytes);

        /*Configure config = Configure.builder().bind("examResults",policy).build();
        ByteArrayInputStream bais = new ByteArrayInputStream(mergedDocument);
        XWPFTemplate template = XWPFTemplate.compile("C:\\Users\\IGS\\Documents\\CSB_EProof\\Cert sample\\Result letter templates\\test_template_2.docx",config).render(
                new HashMap<String, Object>() {{
                    put("examResults", markDtoList);
                }}
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        template.write(baos);*/
//        docxUtil.createTempDocxFile(mergedDocument);

        inputStream.close();


        return ResponseEntity.ok().headers(header).body(mergedDocument);
    }

    @GetMapping("/unoTesting")
    public ResponseEntity unoTesting() throws Exception {
        XComponentContext xContext = Bootstrap.bootstrap();

        XMultiComponentFactory xMCF = xContext.getServiceManager();

        Object oDesktop = xMCF.createInstanceWithContext(
                "com.sun.star.frame.Desktop", xContext);

        XDesktop xDesktop = UnoRuntime.queryInterface(
                XDesktop.class, oDesktop);



        XComponentLoader xCompLoader = UnoRuntime
                .queryInterface(com.sun.star.frame.XComponentLoader.class, xDesktop);

        String sUrl = "file:///" + "C:\\Users\\IGS\\Documents\\CSB_EProof\\Cert sample\\Result letter templates\\test_template_2.docx";

        PropertyValue[] propertyValues = new PropertyValue[0];

        propertyValues = new PropertyValue[1];
        propertyValues[0] = new PropertyValue();
        propertyValues[0].Name = "Hidden";
        propertyValues[0].Value = true;

        XComponent xComp = xCompLoader.loadComponentFromURL(
                sUrl, "_blank", 0, propertyValues);

        XStorable xStorable = UnoRuntime
                .queryInterface(XStorable.class, xComp);

        propertyValues = new PropertyValue[2];
        propertyValues[0] = new PropertyValue();
        propertyValues[0].Name = "Overwrite";
        propertyValues[0].Value = true;
        propertyValues[1] = new PropertyValue();
        propertyValues[1].Name = "FilterName";
        propertyValues[1].Value = "writer_pdf_Export";

        xStorable.storeToURL("file:///C:/Users/IGS/Documents/CSB_EProof/Cert sample/Result letter templates/unotesting.pdf",propertyValues);

        return null;
    }


    @GetMapping("/tryDocx4jToPdf")
    public ResponseEntity tryDocx4jToPdf() throws Exception {
        WordprocessingMLPackage pkg = Docx4J.load(new File("D:\\Work Folder\\CSB Eproof\\Cert sample\\Result letter templates\\test_template.docx"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Docx4J.toPDF(pkg,baos);

        baos.flush();
        baos.close();

        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test.pdf")
                .build()
        );

        return ResponseEntity.ok().headers(header).body(baos.toByteArray());

    }

    @Autowired
    MinioUtil minioUtil;
    @GetMapping("/uploadFileToMinio")
    public ResponseEntity uploadFileToMinio() throws Exception {
        minioUtil.uploadFile("EEEEE.docx",new FileInputStream(tmpSource));
        logger.info("Upload file to Minio");
        return ResponseEntity.ok().body("");
     }

    @GetMapping("/deleteFileToMinio")
    public ResponseEntity deleteFileFromMinio() throws Exception {
        minioUtil.deleteFile("EEEEE.docx");
        logger.info("Delete file to Minio");
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/getFileToMinio")
    public ResponseEntity getFileToMinio() throws Exception {
        logger.info("Download file from Minio");
         return ResponseEntity.ok().body(IOUtils.toByteArray(minioUtil.getFileAsStream("/letter_template/pass_template.docx")));
    }

    private final EmailTemplateRepository emailTemplateRepository;
     private final EmailUtil emailUtil;

    @GetMapping("/testEmail2")
    public String testEmail2() throws Exception {
        EmailTemplate notifyEmailTemplate = emailTemplateRepository.findByName(Constants.EMAIL_TEMPLATE_NOTIFY);

        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("application_name","*#*application_name*#*");
        replaceMap.put("examination_date","*#*examination_date*#*");
        replaceMap.put("eproof_document_url","*#*eproof_document_url*#*");
        return emailUtil.getRenderedHtml(notifyEmailTemplate.getBody(),replaceMap);    }

    @GetMapping("/testEmail")
    public void testEmail(){

    }

    @PostMapping("/gcisBatchEmail/manualResendBatch")
    public Object manualResendBatch(@RequestBody ManualResendBatchEmailRequest manualResendBatchEmailRequest) throws Exception {
        GcisBatchEmail gcisBatchEmail =gcisBatchEmailRepository.findById(manualResendBatchEmailRequest.getGcisBatchEmailId()).orElseThrow(()->new GenericException("gcis.batch.email.not.found","GCIS batch email not found"));
        return gcisBatchEmailService.scheduleBatchEmail(gcisBatchEmail, LocalDateTime.parse(manualResendBatchEmailRequest.getScheduleTime(), DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN)));
    }

    @GetMapping("/gcisBatchEmail/manualEnquire/{gcisBatchEmailId}")
    public Object manualEnquire(@PathVariable Long gcisBatchEmailId) throws Exception {
        GcisBatchEmail gcisBatchEmail =gcisBatchEmailRepository.findById(gcisBatchEmailId).orElseThrow(()->new GenericException("gcis.batch.email.not.found","GCIS batch email not found"));
        return gcisBatchEmailService.enquireUploadStatus(gcisBatchEmail.getId());
     }


}
