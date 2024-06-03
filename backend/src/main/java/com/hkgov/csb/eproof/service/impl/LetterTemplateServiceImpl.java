package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.FileRepository;
import com.hkgov.csb.eproof.dao.LetterTemplateRepository;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.LetterTemplateService;
import com.hkgov.csb.eproof.util.MinioUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.function.IOUnaryOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class LetterTemplateServiceImpl implements LetterTemplateService {

    private final LetterTemplateRepository letterTemplateRepository;
    private final FileRepository fileRepository;
    private final MinioUtil minioUtil;

    @Override
    public InputStream getTemplateByNameAsInputStream(@NotBlank String templateName) {
        if (StringUtils.isEmpty(templateName)){
            throw new GenericException(ExceptionEnums.TEMPLATE_NAME_CANNOT_BE_NULL);
        }
        LetterTemplate letterTemplate = letterTemplateRepository.getByName(templateName);
        if (letterTemplate == null){
            throw new GenericException(ExceptionEnums.TEMPLATE_NOT_EXIST);
        }

        File file = fileRepository.findById(letterTemplate.getFileId()).orElseThrow(()->new GenericException(ExceptionEnums.TEMPLATE_NOT_EXIST));

        return  new ByteArrayInputStream(minioUtil.getFileAsByteArray(file.getPath()));
    }

    @Override
    public byte[] getTemplateByNameAsByteArray(String templateName) throws IOException {
        InputStream inputStream = this.getTemplateByNameAsInputStream(templateName);

        return IOUtils.toByteArray(inputStream);
    }
}
