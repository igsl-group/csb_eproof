package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    Page<Role> getAllRole(Pageable pageable);

    Role getRoleByCode(String code);

    Role updateRole(RoleDto request);

    Role createRole(RoleDto request);

    Role removeRole(String code);

    List<Role> getAllRole();

    Page<Role> search(Pageable pageable, String keyword);
}
