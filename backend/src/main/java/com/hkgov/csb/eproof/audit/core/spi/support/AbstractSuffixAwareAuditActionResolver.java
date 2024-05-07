package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.spi.AuditActionResolver;

/**
 * Abstract class that encapsulates the required suffixes.
 */
public abstract class AbstractSuffixAwareAuditActionResolver implements AuditActionResolver {

    private final String successSuffix;

    private final String failureSuffix;

    protected AbstractSuffixAwareAuditActionResolver(final String successSuffix, final String failureSuffix) {

        if (successSuffix == null) {
            throw new IllegalArgumentException("successSuffix cannot be null.");
        }

        if (failureSuffix == null) {
            throw new IllegalArgumentException("failureSuffix cannot be null.");
        }


        this.successSuffix = successSuffix;
        this.failureSuffix = failureSuffix;
    }

    protected final String getSuccessSuffix() {
        return this.successSuffix;
    }

    protected final String getFailureSuffix() {
        return this.failureSuffix;
    }
}
