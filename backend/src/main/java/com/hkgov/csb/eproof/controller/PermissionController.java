package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.entity.Permission;
import com.hkgov.csb.eproof.mapper.PermissionMapper;
import com.hkgov.csb.eproof.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Secured({"ACCESS_CONTROL_VIEWER"})
    @GetMapping("/getAllPermission")
    public Page<PermissionDto> getAllPermission(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                @RequestParam(defaultValue = "code") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<Permission> permissions = permissionService.getAllPermission(pageable);
        List<PermissionDto> dtoList = permissions
                .stream()
                .map(PermissionMapper.INSTANCE::sourceToDestination)
                .toList();
        return new PageImpl<>(dtoList, pageable, permissions.getTotalElements());
    }

    @Secured({"ACCESS_CONTROL_VIEWER"})
    @GetMapping("/get")
    public PermissionDto getPermission(@RequestParam String code) {
        return PermissionMapper.INSTANCE.sourceToDestination(permissionService.getPermissionByCode(code));
    }

    @Secured({"ACCESS_CONTROL_VIEWER"})
    @GetMapping("/dropdown")
    public List<PermissionDto> getPermissionDropdown() {
        return PermissionMapper.INSTANCE.sourceToDestinationList(permissionService.getAllPermission());
    }
}
