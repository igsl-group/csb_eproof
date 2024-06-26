package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AuditLogService {

    Page<AuditLog> getAllAuditLogs(Pageable pageable);

    void log(AuditLog auditLog);

    Page<AuditLog> search(Pageable pageable, String keyword);

    int purgeAuditLogByRetentionDays(int auditRetentionDays);

    int purgeAuditLogByMaxNo(int auditLogMaxNo);

    byte[] getAuditLogsCsv(LocalDate from, LocalDate to);
}
