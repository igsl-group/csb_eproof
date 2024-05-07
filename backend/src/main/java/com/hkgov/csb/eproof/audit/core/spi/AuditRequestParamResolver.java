package com.hkgov.csb.eproof.audit.core.spi;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditRequestParamResolver {

    String resolveFrom(HttpServletRequest request);
}
