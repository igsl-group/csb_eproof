package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto {
    private Long id;

    private String description;

    private String code;

    public Long getId() {
        return id;
    }
}
