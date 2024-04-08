package com.hkgov.ceo.pms.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.dto.RoleDto;
import com.hkgov.ceo.pms.entity.Role;
import com.hkgov.ceo.pms.entity.Views;
import com.hkgov.ceo.pms.mapper.RoleMapper;
import com.hkgov.ceo.pms.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hkgov.ceo.pms.config.AuditTrailConstants.ROLE_WORDING;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Secured({"ACCESS_CONTROL_VIEWER"})
    @JsonView(Views.Public.class)
    @GetMapping("/dropdown")
    public List<RoleDto> getRoleDropdown() {
        return RoleMapper.INSTANCE.sourceToDestinationList(roleService.getAllRole());
    }

    @Secured({"ACCESS_CONTROL_VIEWER"})
    @GetMapping("/get")
    public RoleDto getRole(@RequestParam String code) {
        return RoleMapper.INSTANCE.sourceToDestination(roleService.getRoleByCode(code));
    }

    @Audit(action = "Update", resourceWording = ROLE_WORDING, resourceResolverName = "roleResourceResolver")
    @Secured({"ACCESS_CONTROL_MAINTENANCE"})
    @PatchMapping("/update")
    public RoleDto updateRole(@RequestBody RoleDto request) {
        return RoleMapper.INSTANCE.sourceToDestination(roleService.updateRole(request));
    }

    @Audit(action = "Create", resourceWording = ROLE_WORDING, resourceResolverName = "roleResourceResolver")
    @Secured({"ACCESS_CONTROL_MAINTENANCE"})
    @PostMapping("/create")
    public RoleDto createRole(@RequestBody RoleDto request) {
        return RoleMapper.INSTANCE.sourceToDestination(roleService.createRole(request));
    }

    @Audit(action = "Delete", resourceWording = ROLE_WORDING, resourceResolverName = "roleResourceResolver")
    @Secured({"ACCESS_CONTROL_MAINTENANCE"})
    @PatchMapping("/remove")
    public RoleDto removeRole(@RequestParam String code) {
        return RoleMapper.INSTANCE.sourceToDestination(roleService.removeRole(code));
    }

    @Secured({"ACCESS_CONTROL_VIEWER"})
    @GetMapping("/search")
    public Page<RoleDto> search(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "code") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<Role> roles = roleService.search(pageable, keyword);
        return roles.map(RoleMapper.INSTANCE::sourceToDestination);
    }
}
