package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.service.PermissionService;
import com.hkgov.csb.eproof.util.Result;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permission")
@Transactional(rollbackFor = Exception.class)
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @GetMapping("/getAll")
    public Result<List<PermissionDto>> getAll(){
        return Result.success(permissionService.getAll());
    }
}
