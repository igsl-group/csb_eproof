package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.RoleHasPermissionRepository;
import com.hkgov.csb.eproof.dao.RoleRepository;
import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import com.hkgov.csb.eproof.entity.RoleHasPermission;
import com.hkgov.csb.eproof.mapper.RoleMapper;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author David
* @description 针对表【role】的数据库操作Service实现
* @createDate 2024-04-23 14:06:28
*/
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleHasPermissionRepository hasPermissionRepository;
    private final AuditLogService auditLogService;
    @Override
    public Boolean createRole(RoleDto requestDto) {
        Role role = RoleMapper.INSTANCE.destinationToSource(requestDto);
        role = roleRepository.save(role);
        auditLogService.addLog("Create","Create role " +role.getName(), requestDto);
        return Objects.nonNull(role);
    }

    @Override
    public Boolean removeRole(Long id) {
        Role role = roleRepository.findById(id).orElse(null);

        if(Objects.nonNull(role)){
            roleRepository.deleteById(id);
            auditLogService.addLog("Delete","Delete role " +role.getName(), null);
        }
        return true;
    }

    @Override
    public Boolean updateRole(Long id, RoleDto requestDto) {

        deletePermissionMapping(id);

        Role role = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        RoleMapper.INSTANCE.partialUpdate(role, requestDto);
        role = roleRepository.save(role);
//        List<RoleHasPermission> roles = requestDto.getPermissions().stream().map(x -> new RoleHasPermission(null,id,x.getId())).toList();
//        hasPermissionRepository.saveAll(roles);
        auditLogService.addLog("Update","Update role " + role.getName() + " information", requestDto);
        return Objects.nonNull(role);

    }

    @Transactional
    void deletePermissionMapping(Long roleId) {
        List<RoleHasPermission> permissions = hasPermissionRepository.getAllByRoleId(roleId);
        if (permissions != null && !permissions.isEmpty()) {
            List<Long> permissionIds = permissions.stream()
                    .map(RoleHasPermission::getId)
                    .collect(Collectors.toList());
            hasPermissionRepository.deleteAllById(permissionIds);
        }
    }

    @Override
    public List<RoleDto> roles() {
        return roleRepository.findAll().stream().map(RoleMapper.INSTANCE::sourceToDestination).toList();
    }

    @Override
    public Page<Role> getAllRolePage(Pageable pageable) {
        var role = roleRepository.findAll(pageable);
        return role;
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.findById(id).orElse(null);
    }


}




