package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.BatchEmailLogDto;
import com.hkgov.csb.eproof.mapper.GcisBatchEmailMapper;
import com.hkgov.csb.eproof.service.GcisBatchEmailService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batchEmailLog")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BatchEmailLogController {
    private final GcisBatchEmailService gcisBatchEmailService;
    @GetMapping("/list")
    public Result<Page<BatchEmailLogDto>> batchEmailList(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size,
                                                         @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "id") String... sortField){
        Pageable pageable = PageRequest.of(page,size,sortDirection,sortField);
        return Result.success(gcisBatchEmailService.batchEmailList(pageable,keyword).map(GcisBatchEmailMapper.INSTANCE::toDto));
    }

    @PostMapping("/download/{gcisBatchEmailId}")
    public ResponseEntity getFileToMinio(@PathVariable Long gcisBatchEmailId) {
        return gcisBatchEmailService.downloadBatchXml(gcisBatchEmailId);
    }
}
