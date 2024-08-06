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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
                             @Schema(type = "string", allowableValues = { "IMPORTED","GENERATED","SIGN_ISSUE","NOTIFY", "ANY","VALID","INVALID"})   @PathVariable String searchType,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                             @RequestParam(defaultValue = "id") String... sortField) throws AccessDeniedException {

        String requiredPermission = "";
        List<String> certStageList = List.of(CertStage.IMPORTED.name(),CertStage.GENERATED.name(),CertStage.SIGN_ISSUE.name(),CertStage.NOTIFY.name(),CertStage.COMPLETED.name(),CertStage.VOIDED.name());
        List<String> certStatusList = List.of(CertStatus.PENDING.name(),CertStatus.SUCCESS.name(),CertStatus.IN_PROGRESS.name(),CertStatus.FAILED.name(), CertStatus.SCHEDULED.name());
        switch (searchType) {
            case "ANY" -> {
                requiredPermission = Permissions.CERT_SEARCH_IMPORT.name();
            }

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
//        permissionService.manualValidateCurrentUserPermission(List.of(requiredPermission));

        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);

        // set certStatusList to null to show all cert regardless what status
        Page<CertInfo> searchResult = certInfoService.search(request,certStageList,certStatusList ,pageable);

        List<CertInfoDto> resultList = CertInfoMapper.INSTANCE.toDtoList(searchResult.getContent());
        /*resultList.forEach(x->{
            if(Objects.nonNull(x.getCertEproof())){
                try {
                    String encodedToken = URLEncoder.encode(x.getCertEproof().getToken(), StandardCharsets.UTF_8.name());
                    x.setUrl(x.getCertEproof().getUrl()+encodedToken);
                    x.setCertEproof(null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });*/
        Page<CertInfoDto> returnResult = new PageImpl<>(resultList, pageable, searchResult.getTotalElements());

        return Result.success(returnResult);
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,value = "/batch/import/{examProfileSerialNo}")
    @Transactional(rollbackFor = Exception.class)
    public Result batchImport(@PathVariable String examProfileSerialNo, @RequestPart("file") MultipartFile file){
        CsvUtil csvUtil = new CsvUtil();
        List<CertImportDto> csvData = csvUtil.getCsvData(file, CertImportDto.class);
        return Result.success(certInfoService.batchImport(examProfileSerialNo,csvData));
    }
    @PostMapping("/batch/dispatch/{examProfileSerialNo}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> dispatch(@PathVariable String examProfileSerialNo, @RequestParam CertStage currentStage){
        return Result.success(certInfoService.dispatch(examProfileSerialNo,currentStage));
    }

    @PostMapping("/batch/scheduleMail/{examProfileSerialNo}")
    @Transactional(rollbackFor = Exception.class)
    public Result batchScheduleMail(
            @PathVariable String examProfileSerialNo,
            @RequestBody InsertGcisBatchEmailDto insertGcisBatchEmailDto
    ){
        certInfoService.changeCertStatusToScheduled(examProfileSerialNo,CertStage.NOTIFY);
        certInfoService.insertGcisBatchEmail(examProfileSerialNo,insertGcisBatchEmailDto);
        return Result.success();
    }

    @PostMapping("/batch/generate/{examProfileSerialNo}")
    @Operation(summary = "Generate cert pdf in batch mode.",description = "Generate all pdf under provided exam serial no. If error encountered during the generation process, not yet generated cert will be updated to 'FAILED' status. ")
    public Result batchGeneratePdf(@PathVariable String examProfileSerialNo) throws Exception {
        certInfoService.changeCertStatusToInProgress(examProfileSerialNo,CertStage.GENERATED);

//        Thread.sleep(20000);

        certInfoService.batchGeneratePdf(examProfileSerialNo);

        return Result.success();
    }


   /* @PostMapping("/batch/signAndIssue/{examProfileSerialNo}")
    public Result signAndIssue(@PathVariable String examProfileSerialNo){
        certInfoService.changeCertStatusToInProgress(examProfileSerialNo,CertStage.SIGN_ISSUE);
        certInfoService.batchSignAndIssue(examProfileSerialNo);
        return Result.success();
    }*/

    @PostMapping("/batch/updateEmail")
    public Result updateEmail(@RequestBody UpdateEmailDto updateEmailDto){
        return Result.success(certInfoService.updateEmail(updateEmailDto));
    }

    @PostMapping("/batch/updatePersonalParticular")
    public Result updatePersonalParticular(@RequestBody UpdatePersonalDto personalDto){
        return Result.success(certInfoService.updatePersonalParticular(personalDto));
    }

    @PostMapping("/single/updatePersonalParticular/{certInfoId}")
    public Result updatePersonalParticular(@PathVariable Long certInfoId,@RequestBody UpdatePersonalDto personalDto){
        certInfoService.updatePersonalParticularById(certInfoId,personalDto);
        return Result.success();
    }

    @PostMapping("/single/updateResult/{certInfoId}")
    public Result updateResult(@PathVariable Long certInfoId,@RequestBody UpdateResultDto resultDto){
        return Result.success(certInfoService.updateResult(certInfoId,resultDto));
    }

    @PostMapping("/hold/{certInfoId}")
    public Result hold(@PathVariable Long certInfoId,@RequestBody CertIntoUpHoldDto dto){
        certInfoService.hold(certInfoId,dto.getRemark());
        return Result.success();
    }

    @PostMapping("/resume/{certInfoId}")
    public Result resume(@PathVariable Long certInfoId,@RequestBody CertIntoUpHoldDto dto){
        certInfoService.resume(certInfoId,dto.getRemark());
        return Result.success();
    }

    @DeleteMapping("/delete/{certInfoId}")
    public Result resume(@PathVariable Long certInfoId){
        certInfoService.delete(certInfoId);
        return Result.success();
    }


//  @PreAuthorize("hasRole('Permissions.CERT_SEARCH_INVALID')")
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


    @PutMapping("/eproof/batch/startScheduleSign/{examProfileSerialNo}")
    public Result startScheduledSign(@PathVariable String examProfileSerialNo){

        List<CertInfo>scheduledCert = certInfoService.batchScheduleCertSignAndIssue(examProfileSerialNo);
        if (scheduledCert!=null && !scheduledCert.isEmpty()){
            return Result.success("Scheduled sign and issue started for "+scheduledCert.size()+" cert(s).");
        } else {
            return Result.success("Already scheduled certs found. No new cert scheduled for sign and issue.");
        }
    }

    @GetMapping("/eproof/getNextScheduledSignAndIssueCert/{examProfileSerialNo}")
    public ResponseEntity getNextScheduledSignAndIssueCert(@PathVariable String examProfileSerialNo){

        CertInfo nextScheduledCertForProcessing = certInfoService.getNextScheduledSignAndIssueCert(examProfileSerialNo);
        if(nextScheduledCertForProcessing!=null){
            return ResponseEntity.ok(nextScheduledCertForProcessing.getId());
        } else {
            return ResponseEntity.badRequest().body("No next scheduled cert found.");
        }
    }

    @PostMapping("/uploadSignedPdf/{certInfoId}")
    public Result uploadSignedPdf(@PathVariable Long certInfoId, @RequestPart("file") MultipartFile file) throws Exception {
        certInfoService.uploadSignedPdf(certInfoId,file);
        certInfoService.issueCert(certInfoId);
        return Result.success();
    }

    @PostMapping("/revoke")
    public Result revoke(@RequestParam List<Long> certInfoIdList,@RequestBody CertRevokeDto params) {
        certInfoRenewService.revoke(certInfoIdList,params);
        return Result.success();
    }

    @GetMapping("/getTodo/revoke")
    public Result<CertRevokeDto> getTodoRevoke(){
        return Result.success(certInfoRenewService.getTodoRevoke());
    }
}
