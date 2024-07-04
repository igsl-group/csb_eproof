package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProofDto {
    private String uuid;
    private Integer version;
    private String otp;
}
