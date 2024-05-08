package com.hkgov.ceo.pms.util;

import java.io.Serializable;

public enum ResultCode implements Serializable {
    SUCCESS("200", "access.successful"),
    SYSTEM_EXECUTION_ERROR("400", "system.error"),
    ;


    private final String code;
    private final String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }
}
