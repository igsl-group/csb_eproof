package com.hkgov.csb.eproof.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CertStage {
    IMPORTED("IMPORTED", "Imported"),
    GENERATED("GENERATED", "Generated"),
    SIGN_ISSUE("SIGN_ISSUE", "Sign and issue"),
    NOTIFY("NOTIFY", "Notify"),
    COMPLETED("COMPLETED", "Completed"),
    VOIDED("VOIDED", "Voided");

    private final String code;
    private final String label;

    CertStage(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
