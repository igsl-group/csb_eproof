package com.hkgov.csb.eproof.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * load I18N resource file
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {
    private static final MessageSource MESSAGE_SOURCE = SpringUtil.getBean(MessageSource.class);

    /**
     * Getting messages based on message keys and parameters is delegated to the spring messageSource
     *
     * @param code message code
     * @param args parameters
     * @return get the internationalized translation value
     */
    public static String message(String code, Object... args) {
        try {
            return MESSAGE_SOURCE.getMessage(code, args, Locale.US);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }
}
