package com.hkgov.csb.eproof.audit.core;

import java.io.Serializable;

/**
 * Encapsulates a runtime execution context at advised audit points. Used for diagnostic purposes
 * to provide clear contextual information about any given audit point in case of runtime failures
 * during audit capturing operation, e.g. any assert failures, etc.
 */
public interface AuditPointRuntimeInfo extends Serializable {

    /**
     * @return String representation of this audit point runtime execution context
     */
    default String asString() {
        return null;
    }
}
