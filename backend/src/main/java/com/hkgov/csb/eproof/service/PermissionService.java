package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    Page<Permission> getAllPermission(Pageable pageable);

    Permission getPermissionByCode(String code);

    List<Permission> getAllPermission();
}
