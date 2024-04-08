package com.hkgov.ceo.pms.audit.core.support;

import com.hkgov.ceo.pms.audit.core.AuditActionContext;
import com.hkgov.ceo.pms.audit.core.AuditTrailManager;
import com.hkgov.ceo.pms.dao.AuditLogRepository;
import com.hkgov.ceo.pms.entity.AuditLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@ConditionalOnExpression("'${audit.trail.manager.include}'.contains('DBAuditTrailManager')")
@Component("DBAuditTrailManager")
public class DBAuditTrailManager implements AuditTrailManager {

    private final AuditLogRepository auditLogRepository;

    public DBAuditTrailManager(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void record(AuditActionContext auditActionContext) {
        AuditLog auditLog = AuditLog.Builder.create()
                .userId(auditActionContext.getUserId())
                .principal(auditActionContext.getPrincipal())
                .resource(auditActionContext.getResourceOperatedUpon())
                .action(auditActionContext.getActionPerformed())
                .actionDateTime(auditActionContext.getWhenActionWasPerformed())
                .node(auditActionContext.getApplicationCode())
                .clientIpAddress(auditActionContext.getClientIpAddress())
                .serverIpAddress(auditActionContext.getServerIpAddress())
                .userAgent(auditActionContext.getUserAgent())
                .url(auditActionContext.getRequestUrl())
                .method(auditActionContext.getRequestMethod())
                .requestParams(auditActionContext.getRequestHeaders())
                .requestBody(auditActionContext.getRequestPayload())
                .responseBody(auditActionContext.getReturnValue())
                .status(auditActionContext.getStatus())
                .errorMessage(auditActionContext.getErrormessage())
                .build();
        auditLogRepository.save(auditLog);
    }

    @Override
    public Set<? extends AuditActionContext> getAuditRecords(Map<WhereClauseFields, Object> whereClause) {
        return null;
    }
}
