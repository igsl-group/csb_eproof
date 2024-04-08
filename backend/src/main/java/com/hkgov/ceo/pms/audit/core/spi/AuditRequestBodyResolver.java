package com.hkgov.ceo.pms.audit.core.spi;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditRequestBodyResolver {
    String resolveFrom(HttpServletRequest request);
}
