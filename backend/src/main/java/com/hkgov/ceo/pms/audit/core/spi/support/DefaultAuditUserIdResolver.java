package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.spi.AuditUserIdResolver;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAuditUserIdResolver implements AuditUserIdResolver {

    @Autowired
    private AuthenticatedInfoService authenticatedInfoService;

    @Override
    public Long resolveFrom(Object principal) {
        return Optional.ofNullable(authenticatedInfoService)
                .map(AuthenticatedInfoService::getCurrentUser)
                .map(User::getUserId)
                .orElse(null);
    }
}
