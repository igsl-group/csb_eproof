package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.Permissions;
import com.hkgov.csb.eproof.dto.*;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.mapper.CertInfoMapper;
import com.hkgov.csb.eproof.service.CertInfoRenewService;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.CsvUtil;
import com.hkgov.csb.eproof.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cert")
@RequiredArgsConstructor
public class CertController {

    private final CertInfoService certInfoService;
    private final PermissionService permissionService;
    private final CertInfoRenewService certInfoRenewService;


    @PostMapping("/search/{searchType}")
    @Transactional(rollbackFor = Exception.class)
    public Result searchCert(@RequestBody CertSearchDto request,
                             @PathVariable String searchType,

                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                             @RequestParam(defaultValue = "id") String... sortField) throws AccessDeniedException {

        String requiredPermission = "";
        List<String> certStageList = List.of(CertStage.IMPORTED.name(),CertStage.GENERATED.name(),CertStage.SIGN_ISSUE.name(),CertStage.NOTIFY.name(),CertStage.COMPLETED.name(),CertStage.VOIDED.name());
        List<String> certStatusList = List.of(CertStatus.PENDING.name(),CertStatus.SUCCESS.name(),CertStatus.IN_PROGRESS.name(),CertStatus.FAILED.name());
        switch (searchType) {
            case "IMPORTED" -> {
                requiredPermission = Permissions.CERT_SEARCH_IMPORT.name();
                certStageList = List.of(CertStage.IMPORTED.name());
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
                request.setCertValid(true);
                /*certStageList = List.of(CertStage.COMPLETED.name(), CertStage.SIGN_ISSUE.name());
                certStatusList = List.of(CertStatus.SUCCESS.name());*/
            }
            case "INVALID" -> {
                requiredPermission = Permissions.CERT_SEARCH_INVALID.name();
                request.setCertValid(false);
                /*certStageList = List.of(CertStage.VOIDED.name());
                certStatusList = List.of(CertStatus.SUCCESS.name());*/
            }

            case "BY_CANDIDATE" -> {
                requiredPermission = Permissions.CERT_SEARCH_BY_CAN.name();
            }
            default -> throw new GenericException(ExceptionEnums.ILLEGAL_SEARCH_TYPE);
        }
        permissionService.manualValidateCurrentUserPermission(List.of(requiredPermission));

        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);

        // set certStatusList to null to show all cert regardless what status
        Page<CertInfo> searchResult = certInfoService.search(request,certStageList,certStatusList ,pageable);

        List<CertInfoDto> resultList = CertInfoMapper.INSTANCE.toDtoList(searchResult.getContent());

        Page<CertInfoDto> returnResult = new PageImpl<>(resultList, pageable, searchResult.getTotalElements());

        return Result.success(returnResult);
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,value = "/batch/import/{examProfileSerialNo}")
    @Transactional(rollbackFor = Exception.class)
    public Result batchImport(@PathVariable String examProfileSerialNo, @RequestParam LocalDate examDate, @RequestPart("file") MultipartFile file){
        CsvUtil csvUtil = new CsvUtil();
        List<CertImportDto> csvData = csvUtil.getCsvData(file, CertImportDto.class);
        return Result.success(certInfoService.batchImport(examProfileSerialNo,examDate,csvData));
    }
    @GetMapping("/batch/dispatch")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> dispatch(@RequestParam String examProfileSerialNo, @RequestParam CertStage currentStage){
        return Result.success(certInfoService.dispatch(examProfileSerialNo,currentStage));
    }

    @PostMapping("/batch/generate/{examProfileSerialNo}")
    @Operation(summary = "Generate cert pdf in batch mode.",description = "Generate all pdf under provided exam serial no. If error encountered during the generation process, not yet generated cert will be updated to 'FAILED' status. ")
    public Result batchGeneratePdf(@PathVariable String examProfileSerialNo) throws Exception {
        certInfoService.changeCertStatusToInProgress(examProfileSerialNo,CertStage.GENERATED);

//        Thread.sleep(20000);

        certInfoService.batchGeneratePdf(examProfileSerialNo);

        return Result.success();
    }


    @PostMapping("/batch/signAndIssue/{examProfileSerialNo}")
    public Result signAndIssue(@PathVariable String examProfileSerialNo){
        certInfoService.changeCertStatusToInProgress(examProfileSerialNo,CertStage.SIGN_ISSUE);
        certInfoService.batchSignAndIssue(examProfileSerialNo);
        return Result.success();
    }
    @PostMapping("/batch/updateEmail")
    public Result updateEmail(@RequestBody UpdateEmailDto updateEmailDto){
        return Result.success(certInfoService.updateEmail(updateEmailDto));
    }

    @PostMapping("/batch/updatePersonalParticular")
    public Result updatePersonalParticular(@RequestBody UpdatePersonalDto personalDto){
        return Result.success(certInfoService.updatePersonalParticular(personalDto));
    }

    @PostMapping("/single/updateResult/{certInfoId}")
    public Result updateResult(@PathVariable Long certInfoId,@RequestBody UpdateResultDto resultDto){
        return Result.success(certInfoService.updateResult(certInfoId,resultDto));
    }


    @PreAuthorize("hasRole('Permissions.CERT_SEARCH_INVALID')")
    @PostMapping("/downloadCert")
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
}
