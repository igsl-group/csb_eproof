package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemParameterDto {
    private Long id;
    private String name;
    private String value;
    private String description;
}
