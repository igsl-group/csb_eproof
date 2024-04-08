package com.hkgov.ceo.pms.audit.common.spi;

import com.hkgov.ceo.pms.audit.common.web.ClientInfo;
import org.aspectj.lang.JoinPoint;

/**
 * Interface for resolving the ClientInfo object.
 */
public interface ClientInfoResolver {

    /**
     * Resolve the ClientInfo from the provided arguments and return value.
     *
     * @param joinPoint the point where the join occurred.
     * @param retVal the return value from the method call.
     * @return the constructed ClientInfo object.  Should never return null!
     */
    ClientInfo resolveFrom(JoinPoint joinPoint, Object retVal);
}
