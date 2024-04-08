package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.entity.Views;

@JsonView(Views.Public.class)
public class PermissionDto {
    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
