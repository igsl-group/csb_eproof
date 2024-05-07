package com.hkgov.csb.eproof.audit.core.spi;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditRequestBodyResolver {
    String resolveFrom(HttpServletRequest request);
}
