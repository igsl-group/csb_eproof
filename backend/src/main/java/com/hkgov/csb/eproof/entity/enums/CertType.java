package com.hkgov.csb.eproof.entity.enums;

public enum CertType {
    RESULT_UPDATE("RESULT_UPDATE", "Result Update"),
    INFO_UPDATE("INFO_UPDATE", "Info Update"),
    REISSUE_HISTORICAL_CERT("REISSUE_HISTORICAL_CERT", "Reissue Historical Cert");

    private final String code;
    private final String label;

    CertType(String code, String label) {
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
