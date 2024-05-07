package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.RoleHasPermission;

public interface RoleHasPermissionService {
    RoleHasPermission getRoleHasPermissionByRoleCodeAndPermissionCode(String roleCode, String permissionCode);

}
