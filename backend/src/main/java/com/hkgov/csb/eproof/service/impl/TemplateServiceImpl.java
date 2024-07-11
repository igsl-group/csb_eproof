package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.dao.LetterTemplateRepository;
import com.hkgov.csb.eproof.entity.File;
import com.hkgov.csb.eproof.entity.LetterTemplate;
import com.hkgov.csb.eproof.service.TemplateService;
import com.hkgov.csb.eproof.util.MinioUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService{

    private final LetterTemplateRepository letterTemplateRepository;
    private final MinioUtil minioUtil;

    public TemplateServiceImpl(LetterTemplateRepository letterTemplateRepository, MinioUtil minioUtil) {
        this.letterTemplateRepository = letterTemplateRepository;
        this.minioUtil = minioUtil;
    }

    @Override
    public Page<LetterTemplate> list(Pageable pageable) {
        return letterTemplateRepository.findAll(pageable);
    }

    @Override
    public ResponseEntity downloadTemplate(Long templateId) {
        LetterTemplate letterTemplate = letterTemplateRepository.findById(templateId).orElseThrow(EntityNotFoundException::new);
        File file = letterTemplate.getFile();

        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(ContentDisposition
                .attachment()
                .filename(file.getName())
                .build()
        );

        return ResponseEntity
                        .ok()
                        .headers(header)
                        .body(minioUtil.getFileAsByteArray(file.getPath()));
    }
}
