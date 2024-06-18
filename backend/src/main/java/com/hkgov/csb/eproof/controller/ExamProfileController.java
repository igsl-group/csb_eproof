package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.ExamProfileCreateDto;
import com.hkgov.csb.eproof.dto.ExamProfileDto;
import com.hkgov.csb.eproof.dto.ExamProfileSummaryDto;
import com.hkgov.csb.eproof.dto.ExamProfileUpdateDto;
import com.hkgov.csb.eproof.mapper.ExamProfileMapper;
import com.hkgov.csb.eproof.service.ExamProfileService;
import com.hkgov.csb.eproof.util.Result;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examProfile")
@Transactional(rollbackFor = Exception.class)
public class ExamProfileController {
    @Resource
    private  ExamProfileService examProfileService;

    @PostMapping("/create")
    public Result<Boolean> create(@RequestBody ExamProfileCreateDto requestDto){
        return Result.success(examProfileService.create(requestDto));
    }

    @PatchMapping("/freeze/{id}")
    public Result<Boolean> freeze(@PathVariable String id){
        return Result.success(examProfileService.freeze(id));
    }

    @PatchMapping("/update/{id}")
    public Result<Boolean> update(@PathVariable String id,@RequestBody ExamProfileUpdateDto requestDto){
        return Result.success(examProfileService.update(id,requestDto));
    }

    @GetMapping("/{id}")
    public Result<ExamProfileDto> getexamProfileInfo(@PathVariable String id){
        return Result.success(ExamProfileMapper.INSTANCE.sourceToDestination(examProfileService.getexamProfileInfo(id)));
    }

    @GetMapping("/list")
    public Result<Page<ExamProfileDto>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(defaultValue = "serialNo") String... sortField){
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return Result.success(examProfileService.list(pageable,keyword).map(ExamProfileMapper.INSTANCE::sourceToDestination));
    }

    @GetMapping("/dropDown")
    public Result<List<ExamProfileDto>> dropDown(){
        return Result.success(ExamProfileMapper.INSTANCE.sourceToDestinationList(examProfileService.dropDown()));
    }
    @DeleteMapping("/delete/{examProfileSerialNo}")
    public Result<Boolean> delete(@PathVariable String examProfileSerialNo){
        return Result.success(examProfileService.delete(examProfileSerialNo));
    }

    @GetMapping("/getSummary/{examProfileSerialNo}")
    public Result<ExamProfileSummaryDto> getSummary(@PathVariable String examProfileSerialNo){
        return Result.success(examProfileService.getSummary(examProfileSerialNo));
    }

    @GetMapping("/reset/{examProfileSerialNo}")
    public Result reset(@PathVariable String examProfileSerialNo){
        examProfileService.reset(examProfileSerialNo);
        return Result.success();
    }

}
