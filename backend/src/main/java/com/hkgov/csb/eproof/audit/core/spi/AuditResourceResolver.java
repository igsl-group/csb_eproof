package com.hkgov.csb.eproof.audit.core.spi;

import com.hkgov.csb.eproof.audit.core.AuditTrailManager;
import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import org.aspectj.lang.JoinPoint;

/**
 * An SPI interface needed to be implemented by individual applications requiring an audit trail record keeping
 * functionality, to provide a current resource on which an audit-able action is being performed.
 */
public interface AuditResourceResolver {

    /**
     * Resolve the auditable resource.
     * 
     * @param target the join point that contains the arguments.
     * @param returnValue	The returned value
     * @param audit the Audit annotation that may contain additional information.
     * @return	The resource String.
     */
    String[] resolveFrom(JoinPoint target, Object returnValue, Audit audit);
    
    /**
     * Resolve the auditable resource for an audit-able action that has
     * incurred an exception.
     * 
     * @param target the join point that contains the arguments.
     * @param audit the Audit annotation that may contain additional information.
     * @param exception	The exception incurred when the join point proceeds.
     * @return	The resource String.
     */
    String[] resolveFrom(JoinPoint target, Audit audit, Exception exception);

    default void setAuditFormat(AuditTrailManager.AuditFormats auditFormat) {
    }
}
