package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePersonalDto {
    private String newName;
    private String currentHkid;
    private String newHkid;
    private String currentPassport;
    private String newPassport;
    private String remark;
}
