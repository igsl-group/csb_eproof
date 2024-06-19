package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.EmailTemplateDto;
import com.hkgov.csb.eproof.dto.EmailTemplateUpdateDto;
import com.hkgov.csb.eproof.mapper.EmailTemplateMapper;
import com.hkgov.csb.eproof.service.EmailTemplateService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @GetMapping("/{emailId}")
    public Result<EmailTemplateDto> getexamProfileInfo(@PathVariable Long emailId){
        return Result.success(EmailTemplateMapper.INSTANCE.sourceToDestination(emailTemplateService.email(emailId)));
    }

    @GetMapping("/list")
    public Result<Page<EmailTemplateDto>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(defaultValue = "id") String... sortField){
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return Result.success(emailTemplateService.list(pageable,keyword).map(EmailTemplateMapper.INSTANCE::sourceToDestination));
    }

    @PatchMapping("/update/{emailId}")
    public Result update(@PathVariable Long emailId,@RequestBody EmailTemplateUpdateDto requestDto){
        emailTemplateService.update(emailId,requestDto);
        return Result.success();
    }
}
