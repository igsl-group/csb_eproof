package com.hkgov.csb.eproof.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

;
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode implements Serializable {
    SUCCESS("200", "access.successful"),
    SYSTEM_EXECUTION_ERROR("400", "system.error"),
    HKID_EXITS("1001","hkid.exits"),
    HKID_PANNPORT_ABSENT("1002","hkid.passport.exits"),
    CSV_EXAM_DATE("1003","examDate.error"),
    PASSPORT_EXITS("1004","passport.exits"),
    CSV_LETTER_TYPE("1005","letter.type"),
    CSV_EMAIL_ERROR("1006","email.error"),
    STAGE_ERROR("1007","stage.error")
    ;

    private String code;
    private String msg;

    public static ResultCode getValue(String code) {
        for (ResultCode value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return SYSTEM_EXECUTION_ERROR;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

}
