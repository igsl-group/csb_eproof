package com.hkgov.csb.eproof.audit.core.spi;

public interface AuditStatusResolver {

    String getSuccessStatus();

    String getFailureStatus();
}
