package com.hkgov.ceo.pms.audit.core.spi.support;


import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.auth.AuthenticationRequest;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * Extended implementation of {@link VoidAuditResourceResolver} that show information for login/logout.
 */

@Component
public class AuthenticationAuditResourceResolver extends VoidAuditResourceResolver {

    @Override
    public String[] resolveFrom(JoinPoint target, Object returnValue, Audit audit) {
        if (target != null && target.getArgs().length > 0 && target.getArgs()[0] instanceof AuthenticationRequest authRequest) {
            return new String[]{audit.resourceWording() + authRequest.getLoginId()};
        }
        return super.resolveFrom(target, returnValue, audit);
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Audit audit, final Exception exception) {
        if (target != null && target.getArgs().length > 0 && target.getArgs()[0] instanceof AuthenticationRequest authRequest) {
            return new String[]{audit.resourceWording() + authRequest.getLoginId()};
        }
        return super.resolveFrom(target, audit, exception);
    }
}
