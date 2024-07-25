package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.AuditLogListDto;
import com.hkgov.csb.eproof.mapper.AuditLogMapper;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auditLog")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;
    @GetMapping("/list")
    public Result<Page<AuditLogListDto>> list(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(defaultValue = "id") String... sortField){
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return Result.success(auditLogService.list(pageable,keyword).map(AuditLogMapper.INSTANCE::sourceToDestination));
    }
}
