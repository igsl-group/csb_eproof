package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.spi.AuditRequestParamResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import static com.hkgov.ceo.pms.util.HttpUtils.getParameters;

@Component
public class DefaultAuditRequestParamResolver implements AuditRequestParamResolver {
    @Override
    public String resolveFrom(HttpServletRequest request) {
        return getParameters(request).toString();
    }
}
