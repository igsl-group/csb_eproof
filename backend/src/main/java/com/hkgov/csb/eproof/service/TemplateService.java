package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.dto.LetterTemplateDto;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TemplateService {

    Page<LetterTemplate> list(Pageable pageable);
}
