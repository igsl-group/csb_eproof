package com.hkgov.ceo.pms.audit.core.spi;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditRequestParamResolver {

    String resolveFrom(HttpServletRequest request);
}
