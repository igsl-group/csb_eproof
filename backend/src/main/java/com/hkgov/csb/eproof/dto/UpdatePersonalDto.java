package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePersonalDto {
    private String currentPassport;
    private String currentHkid;
    private String newHkid;
    private String prinewPassport;
    private String remark;
}
