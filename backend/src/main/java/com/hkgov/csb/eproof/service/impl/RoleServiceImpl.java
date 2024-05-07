package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.PermissionRepository;
import com.hkgov.csb.eproof.dao.RoleRepository;
import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.dto.RoleHasPermissionDto;
import com.hkgov.csb.eproof.entity.Role;
import com.hkgov.csb.eproof.entity.RoleHasPermission;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.RoleHasPermissionService;
import com.hkgov.csb.eproof.service.RoleService;
import com.hkgov.csb.eproof.exception.ExceptionConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.hkgov.csb.eproof.config.Constants.SYSTEM_ADMINISTRATOR_CODE;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleHasPermissionService roleHasPermissionService;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, RoleHasPermissionService roleHasPermissionService, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.roleHasPermissionService = roleHasPermissionService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<Role> getAllRole(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleByCode(String code) {
        return findRoleByCode(code);
    }

    @Override
    public Role updateRole(RoleDto request) {
        Role role = findRoleByCode(request.getCode());
        validateIsNotAdminRole(role);
        if (StringUtils.isNotBlank(request.getName())) {
            role.setName(request.getName());
        }
        role.getRoleHasPermissions().clear();
        if (!request.getRoleHasPermissions().isEmpty()) {
            request.getRoleHasPermissions().stream()
                    .map(RoleHasPermissionDto::getPermission)
                    .map(PermissionDto::getCode)
                    .forEach(permissionCode -> {
                        RoleHasPermission roleHasPermission = roleHasPermissionService.getRoleHasPermissionByRoleCodeAndPermissionCode(role.getCode(), permissionCode);
                        if (roleHasPermission == null) {
                            role.addRoleHasPermission(permissionRepository.findByCode(permissionCode));
                        } else {
                            role.addRoleHasPermission(roleHasPermission);
                        }
                    });
        }
        roleRepository.save(role);
        return role;
    }

    @Override
    public Role createRole(RoleDto request) {
        Role role = new Role();
        role.setCode(request.getCode());
        role.setName(request.getName());
        roleRepository.save(role);
        request.getRoleHasPermissions()
                .stream()
                .map(RoleHasPermissionDto::getPermission)
                .map(PermissionDto::getCode)
                .map(permissionRepository::findByCode)
                .forEach(role::addRoleHasPermission);
        return role;
    }

    @Override
    public Role removeRole(String code) {
        Role role = findRoleByCode(code);
        validateIsNotAdminRole(role);
        role.getUserHasRoles()
                .forEach(userHasRole ->
                        userHasRole.setUser(null));
        role.getUserHasRoles().clear();
        role.getRoleHasPermissions().clear();
        roleRepository.delete(role);
        return role;
    }

    private static void validateIsNotAdminRole(Role role) {
        if (SYSTEM_ADMINISTRATOR_CODE.equals(role.getCode())) {
            throw new GenericException(ExceptionConstants.CANNOT_MODIFY_ADMIN_EXCEPTION_CODE, ExceptionConstants.CANNOT_MODIFY_ADMIN_EXCEPTION_MESSAGE);
        }
    }

    private Role findRoleByCode(String code) {
        return Optional.ofNullable(code)
                .map(roleRepository::findByCode)
                .orElseThrow(() -> new GenericException(ExceptionConstants.ROLE_NOT_FOUND_EXCEPTION_CODE, ExceptionConstants.ROLE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public Page<Role> search(Pageable pageable, String keyword) {
        return roleRepository.findByCodeOrName(pageable, keyword);
    }
}
