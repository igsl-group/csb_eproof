package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.AuditTrailManager;
import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Function;

/**
 * Implementation of {@link AuditResourceResolver} that uses the toString version of the return value
 * as the resource.
 */
@Component
public class ReturnValueAsStringResourceResolver implements AuditResourceResolver {

    protected AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
    }


    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval, final Audit audit) {
        if (retval instanceof Collection) {
            final var c = (Collection) retval;
            final var retvals = new String[c.size()];

            var i = 0;
            for (final var iter = c.iterator(); iter.hasNext() && i < c.size(); i++) {
                final var o = iter.next();

                if (o != null) {
                    retvals[i] = toResourceString(o);
                }
            }

            return retvals;
        }

        if (retval instanceof Object[]) {
            final var vals = (Object[]) retval;
            final var retvals = new String[vals.length];
            for (var i = 0; i < vals.length; i++) {
                retvals[i] = toResourceString(vals[i]);
            }

            return retvals;
        }

        return new String[]{toResourceString(retval)};
    }

    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Audit audit, final Exception exception) {
        final var message = exception.getMessage();
        if (message != null) {
            return new String[]{toResourceString(message)};
        }
        return new String[]{toResourceString(exception)};
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return postProcess(AuditTrailManager.toJson(arg));
        }
        return postProcess(arg.toString());
    }

    protected String postProcess(final String value) {
        return resourcePostProcessor.apply(new String[]{value})[0];
    }
}
