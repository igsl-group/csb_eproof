package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.AuditTrailManager;
import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;

import java.util.function.Function;

/**
 * Abstract AuditResourceResolver for when the resource is the same regardless of an exception or not.
 */
public abstract class AbstractAuditResourceResolver implements AuditResourceResolver {

    protected AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
    }

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    public final String[] resolveFrom(final JoinPoint joinPoint, final Object retVal, final Audit audit) {
        return createResource(joinPoint.getArgs());
    }

    @Override
    public final String[] resolveFrom(final JoinPoint joinPoint, final Audit audit, final Exception e) {
        return this.resourcePostProcessor.apply(createResource(joinPoint.getArgs()));
    }

    protected abstract String[] createResource(final Object[] args);
}
