package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.entity.RoleHasPermission;

public interface RoleHasPermissionService {
    RoleHasPermission getRoleHasPermissionByRoleCodeAndPermissionCode(String roleCode, String permissionCode);

}
