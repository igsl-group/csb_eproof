package com.hkgov.ceo.pms.loggin.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.hkgov.ceo.pms.entity.AuditorDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.hkgov.ceo.pms.config.Constants.NO_USER;

public class UserIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(this::getName)
                .orElse(NO_USER);
    }

    private String getName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof AuditorDetails auditorDetails) {
            return auditorDetails.getUserId();
        } else {
            return authentication.getName();
        }
    }
}
