package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto {
    private Long id;

    private String name;

    private String key;

    public Long getId() {
        return id;
    }
}
