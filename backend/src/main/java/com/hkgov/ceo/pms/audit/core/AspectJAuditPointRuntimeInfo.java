package com.hkgov.ceo.pms.audit.core;

import org.aspectj.lang.JoinPoint;

/**
 * Wrapper around AspectJ's JoinPoint containing the runtime execution info for current audit points
 */
public class AspectJAuditPointRuntimeInfo  implements AuditPointRuntimeInfo {

    private JoinPoint currentJoinPoint;

    public AspectJAuditPointRuntimeInfo(JoinPoint currentJoinPoint) {
        this.currentJoinPoint = currentJoinPoint;
    }

    @Override
    public String asString() {
        return this.currentJoinPoint.toLongString();
    }
}
