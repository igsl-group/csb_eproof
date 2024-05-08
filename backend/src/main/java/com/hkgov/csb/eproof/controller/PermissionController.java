package com.hkgov.csb.eproof.controller;

import com.hkgov.ceo.pms.util.Result;
import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.service.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/premission")
@Transactional(rollbackFor = Exception.class)
public class PermissionController {

    @Resource
    private PermissionService permissionService;

  /*  @PostMapping("/create")
    public Result<Boolean> createPre(@RequestBody PermissionDto requestDto){
        return Result.success(permissionService.creatrePer(requestDto));
    }

    @PostMapping("/remove")
    public Result<Boolean> removePre(@RequestParam String id){
        permissionService.removePre(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Boolean> updatePre(@RequestBody PermissionDto requestDto){
        return Result.success(permissionService.updatePre(requestDto));
    }
*/
    @GetMapping("/getAll")
    public Result<List<PermissionDto>> getAll(){
        return Result.success(permissionService.getAll());
    }
}
