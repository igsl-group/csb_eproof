package com.hkgov.csb.eproof.audit.core.spi.support;

import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.audit.core.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ResourceWordingAsAuditResourceResolver implements AuditResourceResolver {
    @Override
    public String[] resolveFrom(final JoinPoint target, final Object returnValue, final Audit audit) {
        return getResourceWordingStrings(audit);
    }

    @Override
    public String[] resolveFrom(final JoinPoint target, final Audit audit, final Exception exception) {
        return getResourceWordingStrings(audit);
    }

    @NotNull
    private static String[] getResourceWordingStrings(Audit audit) {
        return new String[]{audit.resourceWording()};
    }
}
