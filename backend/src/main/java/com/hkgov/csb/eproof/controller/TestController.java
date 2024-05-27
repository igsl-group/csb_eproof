package com.hkgov.csb.eproof.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkgov.csb.eproof.constants.enums.DocumentOutputType;
import com.hkgov.csb.eproof.dao.CertInfoRepository;
import com.hkgov.csb.eproof.entity.*;
import com.hkgov.csb.eproof.service.DocumentService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.DocxUtil;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
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
        byte[] mergedDocx = docxUtil.getMergedDocumentBinary(inputStream,certInfoMap,examMap);
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

        return ResponseEntity.ok().headers(header).body(documentService.getMergedDocument(inputStream, DocumentOutputType.PDF,certInfoMap,examMap));
    }

}
