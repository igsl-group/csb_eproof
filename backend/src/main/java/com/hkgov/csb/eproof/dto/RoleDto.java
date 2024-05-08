package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleDto {
    private Long id;

    private String name;

    private String description;

    private List<Long> permissionList;

    private List<PermissionDto> permissions;

}
