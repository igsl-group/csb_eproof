package com.hkgov.ceo.pms.loggin.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static com.hkgov.ceo.pms.config.Constants.NO_SESSION;

public class SessionConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getSessionId();
        }
        return NO_SESSION;
    }
}
