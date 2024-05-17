package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.PermissionRepository;
import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.mapper.PermissionMapper;
import com.hkgov.csb.eproof.service.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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


    @Override
    public List<PermissionDto> getAll() {
        return permissionRepository.findAll().stream().map(PermissionMapper.INSTANCE::sourceToDestination).toList();
    }
}
