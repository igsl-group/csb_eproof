package com.hkgov.csb.localSigning.controller;


import com.google.common.base.Splitter;
import com.hkgov.csb.localSigning.service.LocalSigningService;
import com.hkgov.csb.localSigning.util.ApiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/localSigning")
public class LocalSigningController {

    static boolean isProcessing = false;

    private final LocalSigningService localSigningService;

    private final ApiUtil apiUtil;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public LocalSigningController(LocalSigningService localSigningService, ApiUtil apiUtil) {
        this.localSigningService = localSigningService;
        this.apiUtil = apiUtil;
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "signingCert", method = RequestMethod.GET)
    public ResponseEntity signingCert(@RequestParam(name="debug", required=false) boolean debug) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {

        if(!localSigningService.init()){
            return ResponseEntity.status(500).body("Init failed");
//            if (initResponseCode == HttpStatus.BAD_REQUEST)
//                return ResponseEntity.status(initResponseCode).body(new ByteArrayResource(errorMessageTooMany.getBytes()));
//            if (initResponseCode == HttpStatus.INTERNAL_SERVER_ERROR)
//                return ResponseEntity.status(initResponseCode).body(new ByteArrayResource(errorMessageDefault.getBytes()));
//            if (initResponseCode == HttpStatus.NOT_FOUND)
//                return ResponseEntity.status(initResponseCode).body(new ByteArrayResource(errorMessageNotFound.getBytes()));
        }

        String publicKeyCert = Base64.getEncoder().encodeToString(localSigningService.getOutputPublicKey().getEncoded());
        String publicKeyFormatted = "-----BEGIN PUBLIC KEY-----" + "\r\n";
        for (final String row: Splitter.fixedLength(64).split(publicKeyCert))
        {
            publicKeyFormatted += row + "\r\n";
        }
        publicKeyFormatted += "-----END PUBLIC KEY-----";

        String out = Base64.getEncoder().encodeToString(publicKeyFormatted.getBytes());
        if(debug)
            out += "\nCN: " + localSigningService.getCommonName();

        return ResponseEntity.ok(out);
    }

    @CrossOrigin
    @PostMapping("/reissueStart/{certInfoRenewId}")
    public ResponseEntity reissueStart(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestPart(name="reason", required=false) String reason,
            @RequestPart(name="location", required=false) String location,
            @RequestPart(name="qr", required=false) String qr,
            @RequestPart(name="keyword", required=false) String keyword,
            @PathVariable Long certInfoRenewId
    ) throws Exception {
        String jwtTokenFromFrontEnd = request.getHeader("Authorization");

        if(jwtTokenFromFrontEnd == null || jwtTokenFromFrontEnd.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token found in request header");
        }

        localSigningService.init();
        String publicKey = localSigningService.getSigningCert();

        localSigningService.processReissue(jwtTokenFromFrontEnd,reason,location,qr,keyword,response,publicKey, certInfoRenewId);

        return ResponseEntity.ok().build();

    }



    @CrossOrigin
    @PostMapping("/start/{examProfileSerialNo}")
    public ResponseEntity start(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestPart(name="reason", required=false) String reason,
            @RequestPart(name="location", required=false) String location,
            @RequestPart(name="qr", required=false) String qr,
            @RequestPart(name="keyword", required=false) String keyword,
            @PathVariable String examProfileSerialNo) throws Exception {

        String jwtTokenFromFrontEnd = request.getHeader("Authorization");

        if(jwtTokenFromFrontEnd == null || jwtTokenFromFrontEnd.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token found in request header");
        }

        localSigningService.init();
        String publicKey = localSigningService.getSigningCert();

        apiUtil.startScheduleSignAndIssue(examProfileSerialNo, jwtTokenFromFrontEnd);

        Long nextCertInfoIdForSigning = apiUtil.getNextCertIdForSigning(examProfileSerialNo,jwtTokenFromFrontEnd);

        while (nextCertInfoIdForSigning != null){

            localSigningService.processSignAndIssue(jwtTokenFromFrontEnd,reason,location,qr,keyword,response,publicKey, nextCertInfoIdForSigning);

/*
            String unsignedJson = apiUtil.getUnsignedJsonForCert(nextCertInfoIdForSigning,jwtTokenFromFrontEnd);
            String signedValue = (String)localSigningService.signJson(unsignedJson).getBody();
            logger.info(signedValue);
            byte[] preparedPdf = apiUtil.prepareEproofPdfForSigning(jwtTokenFromFrontEnd,nextCertInfoIdForSigning,unsignedJson,signedValue);

            this.processSigning(preparedPdf, nextCertInfoIdForSigning,jwtTokenFromFrontEnd,reason,  location, qr, keyword, response,publicKey);
*/

            nextCertInfoIdForSigning = apiUtil.getNextCertIdForSigning(examProfileSerialNo,jwtTokenFromFrontEnd);
        }

        if(nextCertInfoIdForSigning == null){
            isProcessing = false;
        }


        return ResponseEntity.ok().build();
    }



    /*private void processSigning(String examProfileSerialNo, String jwtTokenFromFrontEnd, String reason, String location, String qr, String keyword, HttpServletResponse response) {
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
    }*/


   /* private void processSigning(Long nextCertInfoIdForSigning, String jwtTokenFromFrontEnd, String reason, String location, String qr, String keyword, HttpServletResponse response, String publicKey) {
        try {
            List<String> pdfDownloadedLocation =  apiUtil.downloadCertPdf(nextCertInfoIdForSigning,jwtTokenFromFrontEnd);
            if(pdfDownloadedLocation != null && !pdfDownloadedLocation.isEmpty()){
                String pdfLocation = pdfDownloadedLocation.get(0);
                byte[] signedPdf = localSigningService.getSignedPdf(Files.newInputStream(Path.of(pdfLocation)), publicKey, reason, location, qr, keyword);
                apiUtil.uploadSignedPdf(nextCertInfoIdForSigning,jwtTokenFromFrontEnd,signedPdf);
            }
        } catch (Exception e) {
            logger.warn("Caught exception", e);
        }
    }*/

    @RequestMapping(value = "signJson", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity signString(@RequestPart(name="json") String json) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
        try{
            return localSigningService.signJson(json);
        }catch(Exception e){
            logger.warn("Caught exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("errorMessageDefault");
//			Security.removeProvider(providerPKCS11.getName());
//			providerPKCS11 = null;
//			return actualSignString(inputString, publicK);
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
