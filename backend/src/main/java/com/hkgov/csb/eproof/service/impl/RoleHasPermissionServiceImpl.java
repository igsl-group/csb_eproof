package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.RoleHasPermissionRepository;
import com.hkgov.csb.eproof.entity.RoleHasPermission;
import com.hkgov.csb.eproof.service.RoleHasPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleHasPermissionServiceImpl implements RoleHasPermissionService {
    private final RoleHasPermissionRepository roleHasPermissionRepository;

    public RoleHasPermissionServiceImpl(RoleHasPermissionRepository roleHasPermissionRepository) {
        this.roleHasPermissionRepository = roleHasPermissionRepository;
    }

    @Override
    public RoleHasPermission getRoleHasPermissionByRoleCodeAndPermissionCode(String roleCode, String permissionCode) {
        return roleHasPermissionRepository.findByRoleCodeAndPermissionCode(roleCode, permissionCode);
    }
}
