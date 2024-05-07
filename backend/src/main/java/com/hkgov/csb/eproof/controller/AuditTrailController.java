package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.dto.AuditLogSearchDto;
import com.hkgov.csb.eproof.entity.AuditLog;
import com.hkgov.csb.eproof.mapper.AuditLogMapper;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.util.MediaUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.hkgov.csb.eproof.config.AuditTrailConstants.AUDIT_TRAIL_WORDING;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@RestController
@RequestMapping("/api/v1/auditTrail")
public class AuditTrailController {

    private final AuditLogService auditLogService;

    public AuditTrailController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Audit(action = "Read", resourceWording = AUDIT_TRAIL_WORDING,
            resourceResolverName = "resourceWordingAsAuditResourceResolver", retValResolverName = "voidAuditRetValResolver")
    @Secured({"AUDIT_TRAIL_VIEWER"})
    @GetMapping("/search")
    public Page<AuditLogSearchDto> search(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "actionDateTime") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<AuditLog> auditLogs = auditLogService.search(pageable, keyword);
        return auditLogs.map(AuditLogMapper.INSTANCE::toSearchDto);
    }

    //    @Audit(action = "Read", resourceWording = AUDIT_TRAIL_WORDING,
//            resourceResolverName = "resourceWordingAsAuditResourceResolver", retValResolverName = "voidAuditRetValResolver")
    @Secured({"AUDIT_TRAIL_VIEWER"})
    @GetMapping("/csv")
    public ResponseEntity<Resource> getCsv(@RequestParam LocalDate from,
                                           @RequestParam LocalDate to) {
        byte[] csv = auditLogService.getAuditLogsCsv(from, to);
        return ResponseEntity.ok()
                .contentType(APPLICATION_OCTET_STREAM)
                .headers(MediaUtil.createHeader(false, "Audit_Logs_" + from + "_to_" + to + ".csv"))
                .body(new ByteArrayResource(csv));
    }
}
