package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * Uses the success/failure suffixes when an object is returned (or NULL is returned)
 */
@Component
public class ObjectCreationAuditActionResolver extends AbstractSuffixAwareAuditActionResolver {

    /**
     * Constructs the {@link ObjectCreationAuditActionResolver} with a success suffix and failure
     * suffix.  CANNOT be NULL.
     * 
     * @param successSuffix the suffix to use in the event of a success.
     * @param failureSuffix the suffix to use in the event of a failure.
     */
    public ObjectCreationAuditActionResolver(final String successSuffix, final String failureSuffix) {
        super(successSuffix, failureSuffix);
    }


    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Object retval, final Audit audit) {
        final var action = audit.action();

        return action + (retval == null ? getFailureSuffix() : getSuccessSuffix());
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Exception exception, final Audit audit) {
        return audit.action() + getFailureSuffix();
    }
}
