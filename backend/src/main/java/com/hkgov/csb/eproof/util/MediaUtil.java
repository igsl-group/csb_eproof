package com.hkgov.csb.eproof.util;


public class MediaUtil {

    /*private static final Tika tika = new Tika();

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
    }*/
}

