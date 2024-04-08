package com.hkgov.ceo.pms.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.entity.Views;

@JsonView(Views.Public.class)
public class RoleHasPermissionDto {

    private PermissionDto permission;

    public PermissionDto getPermission() {
        return permission;
    }

    public void setPermission(PermissionDto permission) {
        this.permission = permission;
    }
}
