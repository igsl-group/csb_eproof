package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.AuditTrailManager;
import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.audit.core.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Inspektr ResourceResolver that resolves resource as a target object's toString method call
 */
@Component
public class ObjectToStringResourceResolver implements AuditResourceResolver {

    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    private AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
    }

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Object returnValue, final Audit audit) {
        return resourcePostProcessor.apply(new String[]{toResourceString(target.getTarget())});
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Audit audit, final Exception exception) {
        Map<String, String> values = new HashMap<>();
        values.put("target", toResourceString(target.getTarget()));
        values.put("exception", toResourceString(exception.getMessage()));
        return resourcePostProcessor.apply(new String[]{toResourceString(values)});
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return AuditTrailManager.toJson(arg);
        }
        return arg.toString();
    }
}
