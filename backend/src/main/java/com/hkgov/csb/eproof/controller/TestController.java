package com.hkgov.csb.eproof.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.dto.DocumentMarkDto;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.service.DocumentService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.DocxUtil;

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
    private DocumentService documentService;

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
    public ResponseEntity fillDocument2() throws Exception {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename("test.pdf")
                .build()
        );
        CertInfo certInfo = certInfoRepository.findById(1L).get();
        ExamProfile exam = certInfo.getExamProfile();

        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");

        FileInputStream inputStream = new FileInputStream("C:\\Users\\IGS\\Documents\\CSB_EProof\\Cert sample\\Result letter templates\\test_template.docx");

        return ResponseEntity.ok().headers(header).body(documentService.getMergedDocument(inputStream, DocumentOutputType.PDF,docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap),null));
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

        List<DocumentMarkDto> markDtoList = new ArrayList<>();
        if(certInfo.getAtGrade() != null){
            markDtoList.add(new DocumentMarkDto("AT",certInfo.getAtGrade()));
        }
        if(certInfo.getUcGrade() != null){
            markDtoList.add(new DocumentMarkDto("UC",certInfo.getUcGrade()));
        }
        if(certInfo.getUeGrade() != null){
            markDtoList.add(new DocumentMarkDto("UE",certInfo.getUeGrade()));
        }

        if(certInfo.getBlnstGrade() != null){
            markDtoList.add(new DocumentMarkDto("BLNST","PASSED"));
        }


        Map<String,String> certInfoMap = docxUtil.convertObjectToMap(certInfo,"cert");
        Map<String,String> examMap = docxUtil.convertObjectToMap(exam,"examProfile");


        HashMap<String,List> map = new HashMap<>();
        map.put("examResults",markDtoList);

        FileInputStream inputStream = new FileInputStream("C:\\Users\\IGS\\Documents\\CSB_EProof\\Cert sample\\Result letter templates\\test_template_2.docx");

        byte [] mergedDocument = documentService.getMergedDocument(inputStream, DocumentOutputType.PDF,docxUtil.combineMapsToFieldMergeMap(certInfoMap,examMap),map);
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
}
