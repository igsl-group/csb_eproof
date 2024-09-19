package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.HistoricalResultDto;
import com.hkgov.csb.eproof.dto.HistoricalSearchDto;
import com.hkgov.csb.eproof.dto.UpdateHisApproveDto;
import com.hkgov.csb.eproof.dto.UpdateHistoricalDto;
import com.hkgov.csb.eproof.mapper.CombinedHistoricalResultBeforeMapper;
import com.hkgov.csb.eproof.service.CombinedHisResultBefApproveService;
import com.hkgov.csb.eproof.service.CombinedHistoricalResultBeforeService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historicalResult")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class HistoricalResultController {
    private final CombinedHistoricalResultBeforeService resultBeforeService;
    private final CombinedHisResultBefApproveService resultApproveService;
    @PostMapping("/list")
    public Result<Page<HistoricalResultDto>> list(@RequestBody HistoricalSearchDto searchDto, @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size,
                                                  @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                                  @RequestParam(defaultValue = "id") String... sortField){
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return Result.success(resultBeforeService.list(pageable,searchDto).map(CombinedHistoricalResultBeforeMapper.INSTANCE::sourceToDestination));
    }
    @PostMapping("{id}/valid")
    public Result valid(@PathVariable Long id,@RequestBody UpdateHistoricalDto dto){
        resultBeforeService.valid(id,dto.getRemark());
        return Result.success();
    }

    @PostMapping("{id}/invalid")
    public Result invalid(@PathVariable Long id,@RequestBody UpdateHistoricalDto dto){
        resultBeforeService.invalid(id,dto.getRemark());
        return Result.success();
    }
    @PostMapping("{id}/void")
    public Result updateGrade(@PathVariable Long id,@RequestBody UpdateHistoricalDto dto){
        resultBeforeService.updateGrade(id,dto);
        return Result.success();
    }

    @PatchMapping("{id}/update")
    public Result update(@PathVariable Long id,@RequestBody UpdateHisApproveDto dto){
        resultApproveService.update(id,dto);
        return Result.success();
    }

    @PostMapping("request")
    public Result request(@RequestBody UpdateHisApproveDto dto){
        resultApproveService.request(dto);
        return Result.success();
    }

    @PostMapping("{id}/approve")
    public Result approve(@PathVariable Long id,@RequestBody UpdateHisApproveDto dto){
        resultApproveService.approve(id,dto);
        return Result.success();
    }

    @PostMapping("{id}/reject")
    public Result reject(@PathVariable Long id,@RequestBody UpdateHisApproveDto dto){
        resultApproveService.reject(id,dto);
        return Result.success();
    }

    @DeleteMapping("{id}/remove")
    public Result remove(@PathVariable Long id){
        resultApproveService.remove(id);
        return Result.success();
    }

    @GetMapping("approveList")
    public Result<List<UpdateHisApproveDto>> list(){
        List<UpdateHisApproveDto> list = resultApproveService.list();
        return Result.success(list);
    }
}
