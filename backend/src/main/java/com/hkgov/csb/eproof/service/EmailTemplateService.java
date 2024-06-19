package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.EmailTemplateUpdateDto;
import com.hkgov.csb.eproof.entity.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
* @author 20768
* @description 针对表【email_template】的数据库操作Service
* @createDate 2024-06-17 10:17:32
*/
public interface EmailTemplateService {
    EmailTemplate email(Long id);

    Page<EmailTemplate> list(Pageable pageable, String keyWord);

    void update(Long emailId,EmailTemplateUpdateDto requestDto);
}
