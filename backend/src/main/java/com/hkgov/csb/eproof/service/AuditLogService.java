package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void addLog(String action,String description,Object reqBody);

    Page<AuditLog> list(Pageable pageable, String keyWord);
}
