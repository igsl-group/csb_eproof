package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.audit.core.spi.AuditActionResolver;
import org.aspectj.lang.JoinPoint;

/**
 * Implementation of {@link AuditActionResolver} that can process boolean return values.
 * <p>
 * Return values are basically action + either the success or failure suffix based on the boolean
 * value.
 */
public class BooleanAuditActionResolver extends AbstractSuffixAwareAuditActionResolver {

    /**
     * Constructs the {@link BooleanAuditActionResolver} with a success suffix and failure
     * suffix.  CANNOT be NULL.
     * @param successSuffix the suffix to use in the event of a success.
     * @param failureSuffix the suffix to use in the event of a failure.
     */
    public BooleanAuditActionResolver(final String successSuffix, final String failureSuffix) {
        super(successSuffix, failureSuffix);
    }


    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Object retval, final Audit audit) {
        final var bool = (Boolean) retval;
        final var action = audit.action();

        return action + (bool ? getSuccessSuffix() : getFailureSuffix());
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Exception exception, final Audit audit) {
        return audit.action() + getFailureSuffix();
    }
}
