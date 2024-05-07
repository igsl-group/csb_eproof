package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.PermissionRepository;
import com.hkgov.csb.eproof.entity.Permission;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.hkgov.csb.eproof.exception.ExceptionConstants.PERMISSION_NOT_FOUND_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<Permission> getAllPermission(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    @Override
    public Permission getPermissionByCode(String code) {
        return findPermissionByCode(code);
    }

    @Override
    public List<Permission> getAllPermission() {
        return permissionRepository.findAll();
    }

    private Permission findPermissionByCode(String code) {
        return Optional.ofNullable(permissionRepository.findByCode(code))
                .orElseThrow(() -> new GenericException(PERMISSION_NOT_FOUND_EXCEPTION_CODE, PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
