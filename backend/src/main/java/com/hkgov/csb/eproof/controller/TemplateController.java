package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.LetterTemplateDto;
import com.hkgov.csb.eproof.dto.RoleDto;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import com.hkgov.csb.eproof.mapper.LetterTemplateMapper;
import com.hkgov.csb.eproof.mapper.RoleMapper;
import com.hkgov.csb.eproof.service.TemplateService;
import com.hkgov.csb.eproof.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/template")
public class TemplateController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/list")
    @Transactional(rollbackFor = Exception.class)
    public Result<Page<LetterTemplateDto>> search(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                                  @RequestParam(defaultValue = "id") String... sortField) {
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortField);
        return  Result.success(templateService.list(pageable).map(LetterTemplateMapper.INSTANCE::toDto));
    }

    @GetMapping("/download/{templateId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity search(@PathVariable Long templateId) {

        return templateService.downloadTemplate(templateId);
    }

}
