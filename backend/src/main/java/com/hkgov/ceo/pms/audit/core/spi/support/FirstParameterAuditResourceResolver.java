package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.AuditTrailManager;
import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import com.hkgov.ceo.pms.audit.core.util.AopUtils;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Converts the first argument object into a String resource identifier.
 * If the resource string is set, it will return the argument values into a list,
 * prefixed by the string. otherwise simply returns the argument value as a string.
 */
public class FirstParameterAuditResourceResolver implements AuditResourceResolver {


    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    private String resourceString;

    private AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    public void setResourceString(final String resourceString) {
        this.resourceString = resourceString;
    }

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
    }


    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Object retval, final Audit audit) {
        return toResources(getArguments(joinPoint));
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Audit audit, final Exception exception) {
        return toResources(getArguments(joinPoint));
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return AuditTrailManager.toJson(arg);
        }
        return resourceString != null
            ? this.resourceString + Arrays.asList(arg)
            : arg.toString();
    }

    private Object[] getArguments(final JoinPoint joinPoint) {
        return AopUtils.unWrapJoinPoint(joinPoint).getArgs();
    }

    /**
     * Turn the arguments into a list.
     *
     * @param args the args
     * @return the string[]
     */
    private String[] toResources(final Object[] args) {
        return this.resourcePostProcessor.apply(new String[]{toResourceString(args[0])});
    }
}
