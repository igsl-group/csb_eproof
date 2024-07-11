package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.mapper.RoleMapper;
import com.hkgov.csb.eproof.service.RoleService;
import com.hkgov.csb.eproof.util.Result;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> createRole(@RequestBody RoleDto requestDto){
        return Result.success(roleService.createRole(requestDto));
    }
    @DeleteMapping("/delete/{roleId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> removeRole(@PathVariable Long roleId){
        return Result.success(roleService.removeRole(roleId));
    }
    @PatchMapping("/update/{roleId}")
    public Result<Boolean> updateRole(
            @PathVariable Long roleId,
            @RequestBody RoleDto requestDto){
        return Result.success(roleService.updateRole(roleId,requestDto));
    }

    @GetMapping("/list")
    @Transactional(rollbackFor = Exception.class)
    public Result<Page<RoleDto>> search(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                @RequestParam(defaultValue = "id") String... sortField) {
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return  Result.success(roleService.getAllRolePage(pageable).map(RoleMapper.INSTANCE::sourceToDestination));
    }
    @GetMapping("/{roleId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<RoleDto> getRole(@PathVariable Long roleId){
        return Result.success(RoleMapper.INSTANCE.sourceToDestination(roleService.getRole(roleId)));
    }

}
