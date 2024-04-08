package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * Default resolver.  If a suffix is defined for success and failure, the failure suffix is appended if an exception is
 * thrown.  Otherwise, the success suffix is used.
 */
@Component
public class DefaultAuditActionResolver extends AbstractSuffixAwareAuditActionResolver {

    /**
     * Constructs the resolver with empty values for the two suffixes.
     */
    public DefaultAuditActionResolver() {
        this("","");
    }

    /**
     * Constructs the {@link DefaultAuditActionResolver} with a success suffix.
     * @param successSuffix the suffix to use in the event of a success.
     */
    public DefaultAuditActionResolver(final String successSuffix) {
        super(successSuffix, "");
    }
    
    /**
     * Constructs the {@link DefaultAuditActionResolver} with a success suffix and failure
     * suffix.  CANNOT be NULL.
     * @param successSuffix the suffix to use in the event of a success.
     * @param failureSuffix the suffix to use in the event of a failure.
     */
    public DefaultAuditActionResolver(final String successSuffix, final String failureSuffix) {
        super(successSuffix, failureSuffix);
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Object retval, final Audit audit) {
        return audit.action() + getSuccessSuffix();
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Exception exception, final Audit audit) {
        return audit.action() + getFailureSuffix();
    }
}
