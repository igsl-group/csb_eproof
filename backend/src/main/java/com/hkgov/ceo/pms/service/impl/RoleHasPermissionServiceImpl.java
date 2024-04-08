package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.dao.RoleHasPermissionRepository;
import com.hkgov.ceo.pms.entity.RoleHasPermission;
import com.hkgov.ceo.pms.service.RoleHasPermissionService;
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
