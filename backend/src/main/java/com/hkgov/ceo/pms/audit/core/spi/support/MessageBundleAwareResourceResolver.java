package com.hkgov.ceo.pms.audit.core.spi.support;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is {@link MessageBundleAwareResourceResolver}.
 */
public class MessageBundleAwareResourceResolver extends ReturnValueAsStringResourceResolver {

    private final ApplicationContext context;
    
    public MessageBundleAwareResourceResolver(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Audit audit, final Exception e) {
        var resolved = super.resolveFrom(joinPoint, audit, e);
        return resolveMessagesFromBundleOrDefault(resolved, e);
    }

    private String[] resolveMessagesFromBundleOrDefault(final String[] resolved, final Exception e) {
        var locale = LocaleContextHolder.getLocale();
        var defaultKey = String.join("_",
            StringUtils.splitByCharacterTypeCamelCase(e.getClass().getSimpleName())).toUpperCase();
        return Stream.of(resolved)
            .map(key -> toResourceString(context.getMessage(key, null, defaultKey, locale)))
            .filter(Objects::nonNull)
            .toArray(String[]::new);
    }
}
