package com.hkgov.csb.eproof.audit.core.spi;

public interface AuditUserIdResolver {
    Long resolveFrom(Object principal);
}
