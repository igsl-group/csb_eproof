package com.hkgov.csb.eproof.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CertStatus {
    SUCCESS("SUCCESS", "Success"),
    FAILED("FAILED", "Failed"),
    PENDING("PENDING", "Pending"),
    IN_PROGRESS("IN_PROGRESS", "In progress"),
    SCHEDULED("SCHEDULED", "Scheduled"),
    APPROVED("APPROVED", "Approved"),
    REJECTED("REJECTED", "Rejected"),
    WITHDRAWAL("WITHDRAWAL", "Withdrawal");

    private final String code;
    private final String label;

    CertStatus(String code, String label) {
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
