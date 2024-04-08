package com.hkgov.ceo.pms.audit.core.spi;

public interface AuditErrorMessageResolver {

    String resolveFrom(Exception exception);
}
