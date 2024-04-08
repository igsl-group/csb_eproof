package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.audit.core.spi.AuditResourceResolver;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuditResourceResolver} that not show any information of the resource.
 *
 */
@Component
public class VoidAuditResourceResolver extends ReturnValueAsStringResourceResolver {

    @Override
    public String[] resolveFrom(final JoinPoint target, final Object returnValue, final Audit audit) {
        return new String[]{StringUtils.EMPTY};
    }

}
