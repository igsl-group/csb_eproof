package com.hkgov.csb.eproof.controller;

import com.hkgov.ceo.pms.util.Result;
import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.mapper.RoleMapper;
import com.hkgov.csb.eproof.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@Transactional(rollbackFor = Exception.class)
public class RoleController {
    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    public Result<Boolean> createRole(@RequestBody RoleDto requestDto){
        return Result.success(roleService.createRole(requestDto));
    }
    @PostMapping("/remove")
    public Result<Boolean> removeRole(@RequestParam String id){
        return Result.success(roleService.removeRole(id));
    }
    @PatchMapping("/update")
    public Result<Boolean> updateRole(@RequestBody RoleDto requestDto){
        return Result.success(roleService.updateRole(requestDto));
    }
    @GetMapping("/allRole")
    public Result<List<RoleDto>> getAllRole(){
        return Result.success(roleService.roles());
    }
    @PostMapping("/search")
    public Result<Page<RoleDto>> search(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "id") String... properties) {
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        return  Result.success(roleService.getAllRolePage(pageable,keyword).map(RoleMapper.INSTANCE::sourceToDestination));
    }
    @PostMapping("/getRole")
    public Result<RoleDto> getRole(@RequestParam Long roleId){
        return Result.success(roleService.getRole(roleId));
    }

}
