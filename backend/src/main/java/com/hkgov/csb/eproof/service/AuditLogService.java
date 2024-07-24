package com.hkgov.csb.eproof.service;

public interface AuditLogService {
    void addLog(String action,String description,Object reqBody);
}
