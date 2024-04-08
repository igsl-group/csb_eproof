package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.entity.Views;

import java.util.ArrayList;
import java.util.List;

@JsonView(Views.Public.class)
public class RoleDto {
    private String code;

    private String name;

    @JsonView(Views.Internal.class)
    private List<RoleHasPermissionDto> roleHasPermissions = new ArrayList<>();

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

    public List<RoleHasPermissionDto> getRoleHasPermissions() {
        return roleHasPermissions;
    }

    public void setRoleHasPermissions(List<RoleHasPermissionDto> roleHasPermissions) {
        this.roleHasPermissions = roleHasPermissions;
    }
}
