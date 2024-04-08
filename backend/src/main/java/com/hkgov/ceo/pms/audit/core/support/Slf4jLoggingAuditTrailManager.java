package com.hkgov.ceo.pms.audit.core.support;

import com.hkgov.ceo.pms.audit.core.AuditActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * <code>AuditTrailManager</code> that dumps auditable information to a configured logger.
 */
@ConditionalOnExpression("'${audit.trail.manager.include}'.contains('Slf4jLoggingAuditTrailManager')")
@Component
public class Slf4jLoggingAuditTrailManager extends AbstractStringAuditTrailManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void record(final AuditActionContext auditActionContext) {
        log.info(toString(auditActionContext));
    }

    @Override
    public void removeAll() {
    }
}
