package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.entity.Views;

@JsonView(Views.Public.class)
public class UserHasRoleDto {

    private RoleDto role;


    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }
}
