package com.hkgov.csb.eproof.audit.spring.spi.support;

import com.hkgov.csb.eproof.audit.common.spi.PrincipalResolver;
import org.aspectj.lang.JoinPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves the principal name to the one provided by Spring Security.
 */
@Component("defaultPrincipalResolver")
public class SpringSecurityAuditablePrincipalResolver implements PrincipalResolver {

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Object retval) {
        return getFromSecurityContext();
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Exception exception) {
        return getFromSecurityContext();
    }

    @Override
    public String resolve() {
        return getFromSecurityContext();
    }

    private String getFromSecurityContext() {
        final var securityContext = SecurityContextHolder.getContext();

        if (securityContext == null) {
            return UNKNOWN_USER;
        }

        if (securityContext.getAuthentication() == null) {
            return UNKNOWN_USER;
        }

        final var subject = securityContext.getAuthentication().getName();
        if (subject == null) {
            return UNKNOWN_USER;
        }
        return subject;
    }

}
