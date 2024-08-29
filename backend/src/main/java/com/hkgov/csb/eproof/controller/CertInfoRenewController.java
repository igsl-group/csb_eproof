package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.Permissions;
import com.hkgov.csb.eproof.dto.CertInfoRenewDto;
import com.hkgov.csb.eproof.dto.CertRenewSearchDto;
import com.hkgov.csb.eproof.dto.PrepareEproofPdfRequest;
import com.hkgov.csb.eproof.entity.CertInfoRenew;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.CertInfoRenewMapper;
import com.hkgov.csb.eproof.request.SendEmailRequest;
import com.hkgov.csb.eproof.service.CertInfoRenewService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reissueCert")
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CertInfoRenewController {
    private final CertInfoRenewService certInfoRenewService;
    private final PermissionService permissionService;

    @PostMapping("/generate/{renewCertId}")
    @Operation(summary = "Generate cert pdf in batch mode.",description = "Generate all pdf under provided exam serial no. If error encountered during the generation process, not yet generated cert will be updated to 'FAILED' status. ")
    public Result singleGeneratePdf(@PathVariable Long renewCertId) throws Exception {
//        certInfoRenewService.changeCertStatusToInProgress(renewCertId, CertStage.GENERATED);
        certInfoRenewService.singleGeneratePdf(renewCertId);

        return Result.success();
    }

    @PostMapping("/notify/{renewCertId}")
    public Result notifyCandidate(@PathVariable Long renewCertId, @RequestBody SendEmailRequest requestDto) throws Exception {

        certInfoRenewService.notifyCandidate(renewCertId, requestDto);

        return Result.success();
    }

    @DeleteMapping("/remove/{renewCertId}")
    public Result removeCert(@PathVariable Long renewCertId){
        certInfoRenewService.removeCert(renewCertId);
        return Result.success();
    }

    @PostMapping("/downloadCert")
    @Operation(summary = "Download cert with provided cert ID list.")
    public ResponseEntity downloadPdf(@RequestParam List<Long> certRenewInfoIdList) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename(this.getZipFileName())
                .build()
        );
        byte [] zippedPdfListByteArray = certInfoRenewService.getZippedPdfBinary(certRenewInfoIdList);
        return ResponseEntity.ok()
                .headers(header)
                .body(zippedPdfListByteArray);
    }

    @PostMapping("/search/{searchType}")
    @Transactional(rollbackFor = Exception.class)
    public Result searchCert(@RequestBody CertRenewSearchDto request,
                             @Schema(type = "string", allowableValues = { "RENEWED","GENERATED","SIGN_ISSUE","NOTIFY" })   @PathVariable String searchType,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                             @RequestParam(defaultValue = "id") String... sortField) throws AccessDeniedException {

        String requiredPermission = "";
        List<String> certStageList = List.of(searchType);
        List<String> certStatusList = List.of(CertStatus.PENDING.name(),CertStatus.SUCCESS.name(),CertStatus.IN_PROGRESS.name(),CertStatus.FAILED.name());
        switch (searchType) {
            case "RENEWED" -> {
                requiredPermission = Permissions.CERT_SEARCH_IMPORT.name();
                certStageList = List.of(CertStage.RENEWED.name());
            }
            case "GENERATED" -> {
                requiredPermission = Permissions.CERT_SEARCH_GENERATE.name();
                certStageList = List.of(CertStage.GENERATED.name());
            }
            case "SIGN_ISSUE" -> {
                requiredPermission = Permissions.CERT_SEARCH_SIGNANDISSUE.name();
                certStageList = List.of(CertStage.SIGN_ISSUE.name());
            }
            case "NOTIFY" -> {
                requiredPermission = Permissions.CERT_SEARCH_NOTIFY.name();
                certStageList = List.of(CertStage.NOTIFY.name());
            }
            case "VALID" -> {
                requiredPermission = Permissions.CERT_SEARCH_VALID.name();
              //  request.setCertValid(true);
                /*certStageList = List.of(CertStage.COMPLETED.name(), CertStage.SIGN_ISSUE.name());
                certStatusList = List.of(CertStatus.SUCCESS.name());*/
            }
            case "INVALID" -> {
                requiredPermission = Permissions.CERT_SEARCH_INVALID.name();
             //   request.setCertValid(false);
                /*certStageList = List.of(CertStage.VOIDED.name());
                certStatusList = List.of(CertStatus.SUCCESS.name());*/
            }
            case "BY_CANDIDATE" -> {
                requiredPermission = Permissions.CERT_SEARCH_BY_CAN.name();
            }
            default -> throw new GenericException(ExceptionEnums.ILLEGAL_SEARCH_TYPE);
        }
//        permissionService.manualValidateCurrentUserPermission(List.of(requiredPermission));
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);

        // set certStatusList to null to show all cert regardless what status
        Page<CertInfoRenew> searchResult = certInfoRenewService.search(request,certStageList,certStatusList ,pageable);

        List<CertInfoRenewDto> resultList = CertInfoRenewMapper.INSTANCE.toDtoList(searchResult.getContent());
        Page<CertInfoRenewDto> returnResult = new PageImpl<>(resultList, pageable, searchResult.getTotalElements());

        return Result.success(returnResult);
    }

    @PostMapping("/dispatch/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> dispatch(@PathVariable Long id, @RequestParam CertStage currentStage){
        certInfoRenewService.dispatch(id,currentStage);
        return Result.success();
    }

    private String getZipFileName(){
        return String.format("%s-cert-pdf.zip",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN_2)));
    }

    @GetMapping("/eproof/getUnsignedJson/{certInfoRenewId}")
    public ResponseEntity getUnsignedJson(@PathVariable Long certInfoRenewId){

        return ResponseEntity.ok(certInfoRenewService.prepareEproofUnsignJson(certInfoRenewId));
    }


    @PostMapping("/eproof/prepareEproofPdf/{certInfoRenewId}")
    public ResponseEntity prepareEproofPdf(
            @PathVariable Long certInfoRenewId,
            @RequestBody PrepareEproofPdfRequest prepareEproofPdfRequest
    ) throws Exception {

        byte[] preparedEproofPdf = certInfoRenewService.prepareEproofPdf(certInfoRenewId, prepareEproofPdfRequest);
        return ResponseEntity.ok().body(preparedEproofPdf);
    }

    @PostMapping("/eproof/uploadSignedPdf/{certInfoRenewId}")
    public Result uploadSignedPdf(@PathVariable Long certInfoRenewId, @RequestPart("file") MultipartFile file) throws Exception {
        certInfoRenewService.uploadSignedPdf(certInfoRenewId,file);
        certInfoRenewService.issueCert(certInfoRenewId);
        return Result.success();
    }
}
