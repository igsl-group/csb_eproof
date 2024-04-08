package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import com.hkgov.ceo.pms.dto.DepartmentDto;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import static com.hkgov.ceo.pms.config.Constants.NULL_STRING;

/**
 * Implementation of {@link AuditResourceResolver} that show information in format {message: %s}. <br>
 * %s display Nil if exception is thrown.
 */
@Component
public class DepartmentResourceResolver implements AuditResourceResolver {

    @Override
    public String[] resolveFrom(final JoinPoint target, final Object returnValue, final Audit audit) {
        String value = NULL_STRING;
        if (returnValue instanceof DepartmentDto dto) {
            value = String.format(audit.resourceWording(), dto.getDepartmentName());
        }
        return new String[]{value};
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Audit audit, final Exception exception) {
        return new String[]{String.format(audit.resourceWording(), NULL_STRING)};
    }
}
