package com.hkgov.ceo.pms.audit.common.spi;

import com.hkgov.ceo.pms.audit.common.web.ClientInfo;
import com.hkgov.ceo.pms.audit.common.web.ClientInfoHolder;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation that gets it from the ThreadLocal.
 */
public class DefaultClientInfoResolver implements ClientInfoResolver {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public ClientInfo resolveFrom(final JoinPoint joinPoint, final Object retVal) {
        final var clientInfo = ClientInfoHolder.getClientInfo();
        if (clientInfo != null) {
            return clientInfo;
        }
        log.warn("No ClientInfo could be found.  Returning empty ClientInfo object.");
        return ClientInfo.empty();
    }
}
