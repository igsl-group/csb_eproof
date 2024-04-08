package com.hkgov.ceo.pms.util;

import com.hkgov.ceo.pms.exception.GenericException;
import org.apache.tika.Tika;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_READ_FILE_TYPE_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.FAILED_TO_READ_FILE_TYPE_EXCEPTION_MESSAGE;

public class MediaUtil {

    private static final Tika tika = new Tika();

    private MediaUtil() {
    }

    public static String getMediaType(MultipartFile file) {
        try {
            return MediaType.valueOf(tika.detect(file.getBytes())).getType();
        } catch (IOException e) {
            throw new GenericException(FAILED_TO_READ_FILE_TYPE_EXCEPTION_CODE, FAILED_TO_READ_FILE_TYPE_EXCEPTION_MESSAGE);
        }
    }

    public static MediaType getMediaType(InputStream file) {
        try {
            return MediaType.valueOf(tika.detect(file));
        } catch (IOException e) {
            throw new GenericException(FAILED_TO_READ_FILE_TYPE_EXCEPTION_CODE, FAILED_TO_READ_FILE_TYPE_EXCEPTION_MESSAGE);
        }
    }

    public static HttpHeaders createHeader(Boolean inline, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(getContentDisposition(inline, encode(filename)));
        return headers;
    }

    public static ContentDisposition getContentDisposition(Boolean isInline, String fileName) {
        return Boolean.TRUE.equals(isInline) ?
                ContentDisposition
                        .inline()
                        .filename(fileName)
                        .build()
                :
                ContentDisposition
                        .attachment()
                        .filename(fileName)
                        .build();
    }

    public static String encode(String fileName) {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
    }
}

