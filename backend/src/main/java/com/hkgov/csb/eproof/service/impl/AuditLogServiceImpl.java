package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.AuditLogRepository;
import com.hkgov.csb.eproof.entity.AuditLog;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.service.CsvService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CsvService csvService;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, CsvService csvService) {
        this.auditLogRepository = auditLogRepository;
        this.csvService = csvService;
    }

    @Override
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public void log(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }

    @Override
    public Page<AuditLog> search(Pageable pageable, String keyword) {
        return auditLogRepository.findByPrincipalOrActionOrResource(pageable, keyword);
    }

    @Override
    public int purgeAuditLogByRetentionDays(int auditRetentionDays) {
        return auditLogRepository.purgeAuditLogByRetentionDays(auditRetentionDays);
    }

    @Override
    public int purgeAuditLogByMaxNo(int auditLogMaxNo) {
        List<AuditLog> recordsToBePurged = auditLogRepository.findAuditLogsByMaxNo(auditLogMaxNo);
        auditLogRepository.deleteAll(recordsToBePurged);
        return recordsToBePurged.size();
    }

    @Override
    public byte[] getAuditLogsCsv(LocalDate from, LocalDate to) {
        return csvService.getAuditLogsCsv(auditLogRepository.findAuditLogsByActionDateTime(from, to));
    }
}
