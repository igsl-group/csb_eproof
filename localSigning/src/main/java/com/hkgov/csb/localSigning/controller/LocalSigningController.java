package com.hkgov.csb.localSigning.controller;


import com.hkgov.csb.localSigning.service.LocalSigningService;
import com.hkgov.csb.localSigning.util.ApiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/localSigning")
public class LocalSigningController {



    private final LocalSigningService localSigningService;

    private final ApiUtil apiUtil;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public LocalSigningController(LocalSigningService localSigningService, ApiUtil apiUtil) {
        this.localSigningService = localSigningService;
        this.apiUtil = apiUtil;
    }

    @PostMapping("/start/{examProfileSerialNo}")
    public ResponseEntity start(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestPart(name="reason", required=false) String reason,
            @RequestPart(name="location", required=false) String location,
            @RequestPart(name="qr", required=false) String qr,
            @RequestPart(name="keyword", required=false) String keyword,
            @PathVariable String examProfileSerialNo) throws IOException, InterruptedException {

        String jwtTokenFromFrontEnd = request.getHeader("Authorization");
        jwtTokenFromFrontEnd = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1bmFtZSI6ImFkbWluX3Rlc3QiLCJkcHVzZXJpZCI6ImFkbWluX3Rlc3QiLCJzaWQiOjEsInN1YiI6ImFkbWluX3Rlc3QiLCJpYXQiOjE3MjEyOTIwMDh9.nXQ2C5s5R02rzmXVRsgg2fYkWeXTPJL4Q9OF-eympUU";

        apiUtil.startScheduleSignAndIssue(examProfileSerialNo, jwtTokenFromFrontEnd);
        this.processSigning(examProfileSerialNo,jwtTokenFromFrontEnd,reason,  location, qr, keyword, response);

        return null;
    }

    private void processSigning(String examProfileSerialNo, String jwtTokenFromFrontEnd, String reason, String location, String qr, String keyword, HttpServletResponse response) {
        try {
            localSigningService.init();
            String publicKey = localSigningService.getSigningCert();

            Long nextCertInfoIdForSigning = apiUtil.getNextCertIdForSigning(examProfileSerialNo,jwtTokenFromFrontEnd);

            while (nextCertInfoIdForSigning != null){
                List<String> pdfDownloadedLocation =  apiUtil.downloadCertPdf(nextCertInfoIdForSigning,jwtTokenFromFrontEnd);

                if(pdfDownloadedLocation != null && !pdfDownloadedLocation.isEmpty()){
                    String pdfLocation = pdfDownloadedLocation.getFirst();
                    byte[] signedPdf = localSigningService.getSignedPdf(Files.newInputStream(Path.of(pdfLocation)), publicKey, reason, location, qr, keyword);
                    apiUtil.uploadSignedPdf(nextCertInfoIdForSigning,jwtTokenFromFrontEnd,signedPdf);
                }

                nextCertInfoIdForSigning = apiUtil.getNextCertIdForSigning(examProfileSerialNo,jwtTokenFromFrontEnd);
            }

//            localSigningService.getSignedPdf();
        } catch (Exception e) {
            logger.warn("Caught exception", e);
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "signPdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity signPdf(
            @RequestPart("unsignedPdf") MultipartFile file,
            @RequestPart(name="cert") String publicK,
            @RequestPart(name="reason", required=false) String reason,
            @RequestPart(name="location", required=false) String location,
            @RequestPart(name="qr", required=false) String qr,
            @RequestPart(name="keyword", required=false) String keyword,
            HttpServletResponse response
    ) throws Exception {
        try{
            localSigningService.init();
            String publicCert = localSigningService.getSigningCert();


            return ResponseEntity.ok(localSigningService.getSignedPdf( file.getInputStream(),  publicCert,  reason,  location, qr, keyword));
        }catch(Exception e){
            logger.warn("Caught exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//			Security.removeProvider(providerPKCS11.getName());
//			providerPKCS11 = null;
//			return actualSignPdf( file,  publicK,  reason,  location,  response);
        }
    }







}
