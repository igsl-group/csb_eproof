package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateHistoricalDto {
    private String remark;
    private String subject;
    private Boolean valid;
}
