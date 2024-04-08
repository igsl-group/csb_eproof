package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.entity.AuditLog;
import com.opencsv.bean.MappingStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CsvService {
    <T> List<T> convertToObject(MultipartFile file, Class<T> classType, MappingStrategy<T> strategy);

    List<String> getAttendeeEmailFromCsv(MultipartFile file);

    byte[] getAuditLogsCsv(List<AuditLog> auditLogs);
}
