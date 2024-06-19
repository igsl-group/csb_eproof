package com.hkgov.csb.eproof.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.EmailTemplateRepository;
import com.hkgov.csb.eproof.dto.EmailTemplateUpdateDto;
import com.hkgov.csb.eproof.entity.EmailTemplate;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
* @author 20768
* @description 针对表【email_template】的数据库操作Service实现
* @createDate 2024-06-17 10:17:32
*/
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {
    private final EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplate email(Long id) {
        return emailTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public Page<EmailTemplate> list(Pageable pageable, String keyWord) {
        var examProfile = emailTemplateRepository.findPage(pageable,keyWord);
        return examProfile;
    }

    @Override
    public void update(Long emailId, EmailTemplateUpdateDto requestDto) {
        EmailTemplate emailTemplate = emailTemplateRepository.findById(emailId).orElse(null);
        if(Objects.isNull(emailTemplate)){
            throw new GenericException(ExceptionEnums.TEMPLATE_NOT_EXIST);
        }
        BeanUtil.copyProperties(requestDto,emailTemplate, CopyOptions.create().setIgnoreNullValue(true));
        emailTemplateRepository.save(emailTemplate);
    }
}
