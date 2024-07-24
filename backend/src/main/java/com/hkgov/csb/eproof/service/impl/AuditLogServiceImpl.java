package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.AuditLogRepository;
import com.hkgov.csb.eproof.entity.AuditLog;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.util.HttpUtils;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Resource
    private AuditLogRepository auditLogRepository;

    @Override
    public void addLog(String action, String description, Object reqBody) {
        AuditLog auditLog = new AuditLog();
        auditLog.setIpAddress(HttpUtils.getClientIp());
        auditLog.setComputerInformation(HttpUtils.getUserAgent());
        auditLog.setLogAction(action);
        auditLog.setLogDetails(description);
        if(Objects.nonNull(reqBody)){
            JSONObject jsonObject = new JSONObject(reqBody);
            auditLog.setRequestBody(jsonObject.toString());
        }
        auditLogRepository.save(auditLog);
    }
}
