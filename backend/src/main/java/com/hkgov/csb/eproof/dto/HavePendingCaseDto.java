package com.hkgov.csb.eproof.dto;

import lombok.Data;

@Data
public class HavePendingCaseDto {
    private String hkid;
    private String passport;
    private Boolean havePendingCase;
}
