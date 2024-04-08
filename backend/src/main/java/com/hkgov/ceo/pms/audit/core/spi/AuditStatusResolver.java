package com.hkgov.ceo.pms.audit.core.spi;

public interface AuditStatusResolver {

    String getSuccessStatus();

    String getFailureStatus();
}
