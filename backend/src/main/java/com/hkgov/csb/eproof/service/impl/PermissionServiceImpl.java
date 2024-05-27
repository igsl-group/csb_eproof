package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.PermissionRepository;
import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.mapper.PermissionMapper;
import com.hkgov.csb.eproof.service.AuthenticationService;
import com.hkgov.csb.eproof.service.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

/**
* @author David
* @description 针对表【permission】的数据库操作Service实现
* @createDate 2024-04-26 17:15:34
*/
@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionRepository permissionRepository;
    @Resource
    private AuthenticationService authenticationService;


    @Override
    public List<PermissionDto> getAll() {
        return permissionRepository.defaultSelectAllWithOrdering().stream().map(PermissionMapper.INSTANCE::sourceToDestination).toList();
    }

    @Override
    public void manualValidateCurrentUserPermission(List<String> requiredPermissionList) throws AccessDeniedException {
        User currentUser = authenticationService.getCurrentUser();
        List<String> userPermissionList = currentUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Boolean userHasPermission = false;

        OUTER_LOOP:
        for (String userPermission : userPermissionList) {
            for (String requiredPermission : requiredPermissionList) {
                if(userPermission.equals(requiredPermission)){
                    userHasPermission = true;
                    break OUTER_LOOP;
                }
            }
        }

        if (!userHasPermission) {
            throw new AccessDeniedException("You do not have the required permission to access this resource.");
        }
    }
}
