package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmailDto {
    private String email;
    private String currentHkid;
    private String currentPassport;
}
