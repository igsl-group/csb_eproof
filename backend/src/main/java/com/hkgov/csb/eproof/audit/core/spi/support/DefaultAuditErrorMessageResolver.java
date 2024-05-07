package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.spi.AuditErrorMessageResolver;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAuditErrorMessageResolver implements AuditErrorMessageResolver {
    @Override
    public String resolveFrom(Exception exception) {
        return Optional.ofNullable(exception)
                .map(Exception::getMessage)
                .orElse(null);
    }
}
