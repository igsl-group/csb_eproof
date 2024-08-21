package com.hkgov.csb.eproof.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dto.ExamScoreDto;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.service.DocumentGenerateService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.DocxUtil;

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
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

import java.io.File;
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

    @Autowired
    private CertInfoRepository certInfoRepository;

    @Autowired
    private DocxUtil docxUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentGenerateService documentGenerateService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

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

        FileInputStream inputStream = new FileInputStream("C:\\Users\\IGS\\Desktop\\projects\\csb-eproof\\backend\\src\\main\\resources\\eproofCertTemplate\\fail\\fail_template.docx");
        byte[] pdfBytes = docxUtil.convertDocxToPdf_POI(inputStream);
        return ResponseEntity.ok().headers(header).body(pdfBytes);
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
            markDtoList.add(new ExamScoreDto("Use of Chinese",certInfo.getUcGrade()));
        }
        if(certInfo.getUeGrade() != null){
            markDtoList.add(new ExamScoreDto("Use of English",certInfo.getUeGrade()));
        }

        if(certInfo.getAtGrade() != null){
            markDtoList.add(new ExamScoreDto("Aptitude Test",certInfo.getAtGrade()));
        }
        if(certInfo.getBlnstGrade() != null){
            markDtoList.add(new ExamScoreDto("Basic Law and National Security Law Test", certInfo.getBlnstGrade()));
        }


        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");


        HashMap<String,List> map = new HashMap<>();
        map.put("examResults",markDtoList);

        FileInputStream inputStream = new FileInputStream("/var/csb_eproof/test_template.docx");

        byte [] mergedDocument = documentGenerateService.getMergedDocument(inputStream, DocumentOutputType.PDF,docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap),map);
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
     @GetMapping("/testMinio")
    public ResponseEntity testMinio() throws Exception {
        minioUtil.uploadFile("testing.pdf",new FileInputStream("D:\\Work Folder\\CSB Eproof\\Cert sample\\Result letter templates\\test_template_3.pdf"));
        return ResponseEntity.ok().body("");
     }

    @GetMapping("/testMinio2")
    public ResponseEntity testMinio2() throws Exception {
        return ResponseEntity.ok().body(IOUtils.toByteArray(minioUtil.getFileAsStream("/letter_template/pass_template.docx")));
    }

    @GetMapping("/testEmail")
    public void testEmail(){

    }

}
