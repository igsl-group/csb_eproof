package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
* @author David
* @description 针对表【role】的数据库操作Service
* @createDate 2024-04-23 14:06:28
*/
public interface RoleService{
    Boolean createRole(RoleDto requestDto);

    Boolean removeRole(Long id);

    Boolean updateRole(Long id, RoleDto requestDto);

    List<RoleDto> roles();

    Page<Role> getAllRolePage(Pageable pageable);

    Role getRole(Long id);
}
