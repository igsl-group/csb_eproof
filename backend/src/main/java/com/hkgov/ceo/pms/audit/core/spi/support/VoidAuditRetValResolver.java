package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.spi.AuditRetValResolver;
import org.springframework.stereotype.Component;

@Component
public class VoidAuditRetValResolver implements AuditRetValResolver {
    @Override
    public String resolveFrom(Object retVal) {
        return null;
    }
}
