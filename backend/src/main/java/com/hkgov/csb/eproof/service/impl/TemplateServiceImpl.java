package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.dao.LetterTemplateRepository;
import com.hkgov.csb.eproof.dto.LetterTemplateDto;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import com.hkgov.csb.eproof.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService{

    private final LetterTemplateRepository letterTemplateRepository;

    public TemplateServiceImpl(LetterTemplateRepository letterTemplateRepository) {
        this.letterTemplateRepository = letterTemplateRepository;
    }

    @Override
    public Page<LetterTemplate> list(Pageable pageable) {
        return letterTemplateRepository.findAll(pageable);
    }
}
