package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dto.EproofDataDto;
import com.hkgov.csb.eproof.dto.EproofResponseDto;
import com.hkgov.csb.eproof.dto.PrepareEproofPdfRequest;
import com.hkgov.csb.eproof.dto.ProofDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.CertInfoRenewService;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.ProofService;
import com.hkgov.csb.eproof.util.EProof.EProofConfigProperties;
import com.hkgov.csb.eproof.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EProofController {
    private final ProofService proofService;
    private final CertInfoService certInfoService;
    private final CertInfoRenewService certInfoRenewService;
    private final EProofConfigProperties eProofConfigProperties;



    @PostMapping("/eproof/sendOTP")
    public EproofResponseDto sendOTP(@RequestBody ProofDto requestDto){
        EproofResponseDto responseDto = proofService.sendOTP(requestDto);
        return responseDto;
    }
    @PostMapping("/eproof/eProof-data")
    public EproofResponseDto getData(@RequestBody EproofDataDto requestDto){
        try{
            EproofResponseDto responseDto = proofService.getData(requestDto);
            return responseDto;
        }catch (Exception e){
            if(e instanceof GenericException){
                throw new GenericException(((GenericException) e).getCode(),e.getMessage());
            }
            throw new GenericException(ExceptionEnums.E_PROOF_SYSTEM_ERROR);
        }
    }

    @PostMapping("/eproof/eProof-pdf")
    public EproofResponseDto getPdf(@RequestBody EproofDataDto requestDto){
        try{
            EproofResponseDto responseDto = proofService.getPdf(requestDto);
            return responseDto;
        }catch (Exception e){
            if(e instanceof GenericException){
                throw new GenericException(((GenericException) e).getCode(),e.getMessage());
            }
            throw new GenericException(ExceptionEnums.E_PROOF_SYSTEM_ERROR);
        }
    }

    @PutMapping("/eproof/batch/startScheduleSign/{examProfileSerialNo}")
    public Result startScheduledSign(@PathVariable String examProfileSerialNo){

        Boolean isCertScheduled = certInfoService.batchScheduleCertSignAndIssue(examProfileSerialNo);
        if (isCertScheduled){
            return Result.success("Scheduled sign and issue started.");
        } else {
            return Result.success("Already scheduled certs found. No new cert scheduled for sign and issue.");
        }
    }

    @GetMapping("/eproof/getUnsignedJson/{certInfoId}")
    public ResponseEntity getUnsignedJson(@PathVariable Long certInfoId){

        return ResponseEntity.ok(certInfoService.prepareEproofUnsignJson(certInfoId));
    }

    @PostMapping("/eproof/prepareEproofPdf/{certInfoId}")
    public ResponseEntity prepareEproofPdf(
            @PathVariable Long certInfoId,
            @RequestBody PrepareEproofPdfRequest prepareEproofPdfRequest
    ) throws Exception {

        byte[] preparedEproofPdf = certInfoService.prepareEproofPdf(certInfoId, prepareEproofPdfRequest);
        return ResponseEntity.ok().body(preparedEproofPdf);
    }

    @GetMapping("/eproof/getNextScheduledSignAndIssueCert/{examProfileSerialNo}")
    public ResponseEntity getNextScheduledSignAndIssueCert(@PathVariable String examProfileSerialNo){

        CertInfo nextScheduledCertForProcessing = certInfoService.getNextScheduledSignAndIssueCert(examProfileSerialNo);
        if(nextScheduledCertForProcessing!=null){
            return ResponseEntity.ok(nextScheduledCertForProcessing.getId());
        } else {
            certInfoService.notifyInternalUserSignAndIssueCompleted(examProfileSerialNo);
            return ResponseEntity.badRequest().body("No next scheduled cert found.");
        }
    }

    @PostMapping("/eproof/downloadCert")
    @Operation(summary = "Download cert with provided cert ID list.")
    public ResponseEntity downloadPdf(@RequestParam List<Long> certInfoIdList) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename(this.getZipFileName())
                .build()
        );
        byte [] zippedPdfListByteArray = certInfoService.getZippedPdfBinary(certInfoIdList);
        return ResponseEntity.ok()
                .headers(header)
                .body(zippedPdfListByteArray);
    }

    private String getZipFileName(){
        return String.format("%s-cert-pdf.zip",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_2)));
    }

    @PostMapping("/eproof/uploadSignedPdf/{certInfoId}")
    public Result uploadSignedPdf(@PathVariable Long certInfoId, @RequestPart("file") MultipartFile file) throws Exception {
        certInfoService.uploadSignedPdf(certInfoId,file);
        certInfoService.issueCert(certInfoId);
        return Result.success();
    }

    @GetMapping("/eproof/reissue/getUnsignedJson/{certInfoRenewId}")
    public ResponseEntity getUnsignedJsonForReissueCert(@PathVariable Long certInfoRenewId){

        return ResponseEntity.ok(certInfoRenewService.prepareEproofUnsignJson(certInfoRenewId));
    }

    @PostMapping("/eproof/reissue/prepareEproofPdf/{certInfoRenewId}")
    public ResponseEntity prepareEproofPdfForReissueCert(
            @PathVariable Long certInfoRenewId,
            @RequestBody PrepareEproofPdfRequest prepareEproofPdfRequest
    ) throws Exception {

        byte[] preparedEproofPdf = certInfoRenewService.prepareEproofPdf(certInfoRenewId, prepareEproofPdfRequest);
        // Set access token to null to force the following action to get a new access token
        eProofConfigProperties.setAccessToken(null);

        return ResponseEntity.ok().body(preparedEproofPdf);
    }

    @PostMapping("/eproof/reissue/uploadSignedPdf/{certInfoRenewId}")
    public Result uploadSignedPdfForReissueCert(@PathVariable Long certInfoRenewId, @RequestPart("file") MultipartFile file) throws Exception {
        certInfoRenewService.uploadSignedPdf(certInfoRenewId,file);
        certInfoRenewService.issueCert(certInfoRenewId);
        return Result.success();
    }
}
