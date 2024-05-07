package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.AuditTrailManager;
import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This is {@link ShortenedReturnValueAsStringAuditResourceResolver}.
 */
@Component
public class ShortenedReturnValueAsStringAuditResourceResolver extends ReturnValueAsStringResourceResolver {
    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval, final Audit audit) {
        var resources = super.resolveFrom(auditableTarget, retval, audit);
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return resources;
        }
        if (resources != null) {
            return Arrays.stream(resources)
                .map(r -> StringUtils.abbreviate(r, 125))
                .collect(Collectors.toList())
                .toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        }
        return new String[0];
    }
}

