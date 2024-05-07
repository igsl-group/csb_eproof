package com.hkgov.csb.eproof.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Status {
    APPROVED("APPROVED", "Approved"),
    REJECTED("REJECTED", "Reject"),
    PENDING("PENDING", "Pending");

    private final String code;
    private final String label;

    Status(String code, String label) {
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
