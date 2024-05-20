package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.constants.enums.Permissions;
import com.hkgov.csb.eproof.dto.CertSearchDto;
import com.hkgov.csb.eproof.entity.CertInfo;
import com.hkgov.csb.eproof.entity.enums.CertStage;
import com.hkgov.csb.eproof.entity.enums.CertStatus;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.CertInfoService;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cert")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CertController {

    private final CertInfoService certInfoService;
    private final PermissionService permissionService;

    @PostMapping("/cert/search/{searchType}")
    public Result searchCert(CertSearchDto request,
                             @PathVariable String searchType,

                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                             @RequestParam(defaultValue = "serialNo") String... sortField) throws AccessDeniedException {

        String requiredPermission = "";
        List<String> certStageList = null;
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
                certStageList = List.of(CertStage.COMPLETED.name(), CertStage.SIGN_ISSUE.name());
                certStatusList = List.of(CertStatus.SUCCESS.name());
            }
            case "INVALID" -> {
                requiredPermission = Permissions.CERT_SEARCH_INVALID.name();
                certStageList = List.of(CertStage.VOIDED.name());
                certStatusList = List.of(CertStatus.SUCCESS.name());
            }
            default -> throw new GenericException(ExceptionEnums.ILLEGAL_SEARCH_TYPE);
        }
        permissionService.manualValidateCurrentUserPermission(List.of(requiredPermission));

        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);

        Page<CertInfo> searchResult = certInfoService.search(request,certStageList,certStatusList ,pageable);

        return Result.success(searchResult);
    }
}
