package com.hkgov.ceo.pms.audit.core.spi;

public interface AuditUserIdResolver {
    Long resolveFrom(Object principal);
}
