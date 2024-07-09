package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.SystemParameterDto;
import com.hkgov.csb.eproof.dto.SystemParameterUpDto;
import com.hkgov.csb.eproof.mapper.SystemParameterMapper;
import com.hkgov.csb.eproof.service.SystemParameterService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/systemParameter")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SystemParameterController {
    private final SystemParameterService systemParameterService;
    @GetMapping("/list")
    public Result<Page<SystemParameterDto>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(defaultValue = "id") String... sortField){
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return Result.success(systemParameterService.list(pageable,keyword).map(SystemParameterMapper.INSTANCE::sourceToDestination));
    }

    @PatchMapping("/{id}")
    public Result update(@PathVariable Long id,@RequestBody SystemParameterUpDto requestDto){
        systemParameterService.update(id,requestDto);
        return Result.success();
    }
}
