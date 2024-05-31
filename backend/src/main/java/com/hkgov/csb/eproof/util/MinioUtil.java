package com.hkgov.csb.eproof.util;


import com.hkgov.csb.eproof.config.MinioConfig;
import com.hkgov.csb.eproof.exception.GenericException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;


    @SneakyThrows
    public void uploadFile(String fileName, MultipartFile multipartFile) {
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(minioConfig.getBucket())
                        .object(fileName)
                        .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                        .build()
        );
    }

    // Remember to write content into ByteArrayOutputStream before calling this method !
    @SneakyThrows
    public void uploadFile(String fileName, ByteArrayOutputStream baos) {
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(minioConfig.getBucket()).object(fileName)
                        .stream(is, baos.size(), -1)
                        .build()
        );
    }

    @SneakyThrows
    public void uploadFile(String fileName, InputStream is) {
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(minioConfig.getBucket()).object(fileName)
                        .stream(is, is.available(), -1)
                        .build()
        );
    }



    @SneakyThrows
    public ByteArrayResource getFile(String path) {
        ByteArrayResource file = new ByteArrayResource(IOUtils.toByteArray(minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucket()).object(path).build())));
        return file;
    }

    @SneakyThrows
    public void deleteFile(String path) {

        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(minioConfig.getBucket())
                .object(path)
                .build()
        );
    }

    final String UPPER_CASE_CHAR = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
    final String DIGITS = "0123456789";
    final String PUNCTUATION = "!@#$%^&*()";
    public String generateRandomFileName(String fileName){
        return RandomStringUtils.random(10,UPPER_CASE_CHAR+DIGITS+PUNCTUATION)+ "."+ FileNameUtils.getExtension(fileName);

    }

    public InputStream getFileAsStream(String path) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new GenericException();
        }
    }
}