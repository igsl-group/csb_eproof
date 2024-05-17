package com.hkgov.csb.eproof.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnums {
    // Authentication
    ACCESS_DENIED("access.denied","Your access is denied."),
    ;


    private final String code;
    private final String message;

}
