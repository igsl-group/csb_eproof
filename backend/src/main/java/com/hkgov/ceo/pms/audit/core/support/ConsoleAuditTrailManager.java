package com.hkgov.ceo.pms.audit.core.support;

import com.hkgov.ceo.pms.audit.core.AuditActionContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Simple <code>AuditTrailManager</code> that dumps auditable information to output stream.
 * <p>
 * Useful for testing.
 */
@ConditionalOnExpression("'${audit.trail.manager.include}'.contains('ConsoleAuditTrailManager')")
@Component
public class ConsoleAuditTrailManager extends AbstractStringAuditTrailManager {

    @Override
    public void record(final AuditActionContext auditActionContext) {
        System.out.println(toString(auditActionContext));
    }

    @Override
    public void removeAll() {
    }
}
