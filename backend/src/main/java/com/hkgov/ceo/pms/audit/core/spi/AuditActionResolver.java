package com.hkgov.ceo.pms.audit.core.spi;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import org.aspectj.lang.JoinPoint;

/**
 * An SPI interface needed to be implemented by individual applications requiring an audit trail record keeping
 * functionality, to provide the action taken.
 */
public interface AuditActionResolver {


    /**
     * Resolve the action for the audit event.
     * 
     * @param auditableTarget
     * @param retval	The returned value
     * @param audit the Audit annotation that may contain additional information.
     * @return	The resource String
     */
    String resolveFrom(JoinPoint auditableTarget, Object retval, Audit audit);
    
    /**
     * Resolve the action for the audit event that has incurred
     * an exception.
     * 
     * @param auditableTarget
     * @param exception	The exception incurred when the join point proceeds.
     * @param audit the Audit annotation that may contain additional information.
     * @return	The resource String
     */
    String resolveFrom(JoinPoint auditableTarget, Exception exception, Audit audit);

}
