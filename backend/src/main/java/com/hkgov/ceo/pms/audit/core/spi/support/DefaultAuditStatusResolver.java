package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.spi.AuditStatusResolver;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuditStatusResolver implements AuditStatusResolver {
    @Override
    public String getSuccessStatus() {
        return "Success";
    }

    @Override
    public String getFailureStatus() {
        return "Failure";
    }
}
