package com.hkgov.csb.eproof.audit.core.spi;

public interface AuditErrorMessageResolver {

    String resolveFrom(Exception exception);
}
