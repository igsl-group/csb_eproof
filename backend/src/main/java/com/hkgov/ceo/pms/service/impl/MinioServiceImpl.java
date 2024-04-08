package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.config.MinioConfigurationProperties;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.service.MinioService;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.hkgov.ceo.pms.config.Constants.SLASH;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_GET_FILE_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_GET_FILE_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_REMOVE_FILE_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_REMOVE_FILE_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_UPLOAD_FILE_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_UPLOAD_FILE_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.util.MediaUtil.createHeader;
import static com.hkgov.ceo.pms.util.MediaUtil.getMediaType;

@Service
@Transactional
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final MinioConfigurationProperties minioConfigurationProperties;

    public MinioServiceImpl(MinioClient minioClient, MinioConfigurationProperties minioConfigurationProperties) {
        this.minioClient = minioClient;
        this.minioConfigurationProperties = minioConfigurationProperties;
    }

    @Override
    public void uploadFile(String fileName, MultipartFile multipartFile) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfigurationProperties.getBucket())
                            .object(fileName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .build());
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_UPLOAD_FILE_EXCEPTION_CODE, FAILED_TO_UPLOAD_FILE_EXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public ResponseEntity<Resource> getFile(String path) {
        try {
            ByteArrayResource file = new ByteArrayResource(IOUtils.toByteArray(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfigurationProperties.getBucket())
                            .object(path)
                            .build())));
            return ResponseEntity.ok()
                    .contentType(getMediaType(file.getInputStream()))
                    .headers(createHeader(false, path.substring(path.lastIndexOf("/") + 1)))
                    .body(file);
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_GET_FILE_EXCEPTION_CODE, FAILED_TO_GET_FILE_EXCEPTION_MESSAGE, e);
        }

    }

    @Override
    public void removeFile(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfigurationProperties.getBucket())
                            .object(path)
                            .build());
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_REMOVE_FILE_EXCEPTION_CODE, FAILED_TO_REMOVE_FILE_EXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public void copyFile(String sourcePath, String targetPath) {
        try {
            String[] sourcePathSplit = sourcePath.split(SLASH);
            String targetFile = targetPath + SLASH + sourcePathSplit[sourcePathSplit.length - 1];
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(minioConfigurationProperties.getBucket())
                    .source(CopySource.builder()
                            .bucket(minioConfigurationProperties.getBucket())
                            .object(sourcePath)
                            .build())
                    .object(targetFile)
                    .build());
        } catch (Exception e) {
            throw new GenericException(FAILED_TO_GET_FILE_EXCEPTION_CODE, FAILED_TO_GET_FILE_EXCEPTION_MESSAGE, e);
        }

    }
}

