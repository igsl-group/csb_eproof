package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.spi.AuditRequestBodyResolver;
import com.hkgov.ceo.pms.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuditRequestBodyResolver implements AuditRequestBodyResolver {

    private final HttpUtils httpUtils;

    public DefaultAuditRequestBodyResolver(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
    }

    @Override
    public String resolveFrom(HttpServletRequest request) {
        return httpUtils.getRequestBody(request);
    }
}
