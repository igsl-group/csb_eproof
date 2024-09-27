package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.EmailLogDto;
import com.hkgov.csb.eproof.mapper.EmailMessageMapper;
import com.hkgov.csb.eproof.service.EmailMessageService;
import com.hkgov.csb.eproof.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emailLog")
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class EmailLogController {
    private final EmailMessageService emailMessageService;
    @GetMapping("/list")
    public Result<Page<EmailLogDto>> emailList(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size,
                                               @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(defaultValue = "id") String... sortField){
        Pageable pageable = PageRequest.of(page,size,sortDirection,sortField);
        return Result.success(emailMessageService.emailList(pageable,keyword).map(EmailMessageMapper.INSTANCE::sourceToDestination));
    }
}
