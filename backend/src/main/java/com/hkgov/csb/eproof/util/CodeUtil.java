package com.hkgov.csb.eproof.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.trim;

public class CodeUtil {

    public CodeUtil() {
    }

    public static String convertToCode(String source) {
        return Optional.ofNullable(source)
                .map(StringUtils::trim)
                .map(String::toUpperCase)
                .map(s -> s.replace(SPACE, "_"))
                .map(s -> s.replace("/", "_OR_"))
                .map(s -> s.replace("&", "_N_"))
                .orElse(StringUtils.EMPTY);
    }

    public static String convertToCombinedCode(String first, String second) {
        return convertToCode(trim(first) + SPACE + trim(second));
    }
    public Boolean validEmai(String email){
        if(email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        boolean isValidEmail = matcher.matches();
        return isValidEmail;
    }
}
