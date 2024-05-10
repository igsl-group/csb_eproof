package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.PermissionRepository;
import com.hkgov.csb.eproof.dao.RoleHasPermissionRepository;
import com.hkgov.csb.eproof.dao.RoleRepository;
import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import com.hkgov.csb.eproof.entity.RoleHasPermission;
import com.hkgov.csb.eproof.mapper.RoleMapper;
import com.hkgov.csb.eproof.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
* @author David
* @description 针对表【role】的数据库操作Service实现
* @createDate 2024-04-23 14:06:28
*/
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private RoleHasPermissionRepository hasPermissionRepository;
    @Resource
    private PermissionRepository permissionRepository;
    @Override
    public Boolean createRole(RoleDto requestDto) {
        Role role = roleMapper.INSTANCE.destinationToSource(requestDto);
        role = roleRepository.save(role);
        Long id = role.getId();
        List<RoleHasPermission> roles = requestDto.getPermissionList().stream().map(x -> new RoleHasPermission(null,id,x)).toList();
        hasPermissionRepository.saveAll(roles);
        return Objects.nonNull(role);
    }

    @Override
    public Boolean removeRole(String id) {
        roleRepository.deleteById(Long.parseLong(id));
        return true;
    }

    @Override
    public Boolean updateRole(RoleDto requestDto) {
        Role role = roleMapper.INSTANCE.destinationToSource(requestDto);
        List<Long> ids = hasPermissionRepository.getAllByRoleId(role.getId());
        hasPermissionRepository.deleteAllById(ids);
        role = roleRepository.save(role);
        Long id = role.getId();
        List<RoleHasPermission> roles = requestDto.getPermissionList().stream().map(x -> new RoleHasPermission(null,id,x)).toList();
        hasPermissionRepository.saveAll(roles);
        return Objects.nonNull(role);
    }

    @Override
    public List<RoleDto> roles() {
        return roleRepository.findAll().stream().map(RoleMapper.INSTANCE::sourceToDestination).toList();
    }

    @Override
    public Page<Role> getAllRolePage(Pageable pageable,String keyword) {
        var role = roleRepository.findByCodeOrName(pageable,keyword);
        return role;
    }

    @Override
    public Role getRole(Long id) {
        Role role = roleRepository.getReferenceById(id);
        if(Objects.isNull(role))
            return null;
        return role;
    }


}




