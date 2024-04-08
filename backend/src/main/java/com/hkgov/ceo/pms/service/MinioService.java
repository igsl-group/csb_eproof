package com.hkgov.ceo.pms.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    void uploadFile(String fileName, MultipartFile multipartFile);

    ResponseEntity<Resource> getFile(String fileName);

    void removeFile(String fileName);

    void copyFile(String sourcePath, String targetPath);
}
