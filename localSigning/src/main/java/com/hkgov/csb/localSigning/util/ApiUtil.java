package com.hkgov.csb.localSigning.util;


import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.util.zip.ZipEntry;

@Component
public class ApiUtil {

    @Value("${backend.endpoint.start.schedule.signing}")
    private String startScheduleSigningEndpoint;

    @Value("${backend.endpoint.get.unsigned.json}")
    private String getUnsignedJsonEndpoint;

    @Value("${backend.endpoint.get.unsigned.json.reissue.cert}")
    private String getReissueCertUnsignedJsonEndpoint;

    @Value("${backend.endpoint.prepare.eproof.pdf}")
    private String prepareEproofPdfUrl;

    @Value("${backend.endpoint.prepare.eproof.pdf.reissue.cert}")
    private String prepareReissueCertEproofPdfUrl;

    @Value("${backend.endpoint.get.next.job}")
    private String getNextJobEndpoint;

    @Value("${backend.endpoint.upload.signed.cert}")
    private String uploadSignedCertEndpoint;

    @Value("${backend.endpoint.upload.signed.cert.reissue.cert}")
    private String uploadSignedReissueCertEndpoint;

    Logger logger = LoggerFactory.getLogger(this.getClass());

/*
    @Value("${cert.download.temp.path}")
    private String certDownloadTempPath;
*/

    private static final OkHttpClient CLIENT;

    static {
        try {
            CLIENT = SslUtil.getUnsafeOkHttpClient();
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUnsignedJsonForReissueCert(Long certInfoRenewId, String jwtTokenFromFrontEnd){
        String getUnsignedJsonEndpoint = this.getReissueCertUnsignedJsonEndpoint.replace("{certInfoRenewId}",certInfoRenewId.toString());

        Request request = new Request.Builder()
                .url(getUnsignedJsonEndpoint)
                .get()
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                System.err.println(getUnsignedJsonEndpoint + " Request failed with status code: " + response.code());
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public String getUnsignedJsonForCert(Long certId,String jwtTokenFromFrontEnd){
        String getUnsignedJsonEndpoint = this.getUnsignedJsonEndpoint.replace("{certInfoId}",certId.toString());

        Request request = new Request.Builder()
                .url(getUnsignedJsonEndpoint)
                .get()
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                System.err.println(getUnsignedJsonEndpoint+" Request failed with status code: " + response.code());
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Long getNextCertIdForSigning(String examProfileSerialNo, String jwtTokenFromFrontEnd) throws IOException {
        String getNextJobEndpoint = this.getNextJobEndpoint.replace("{examProfileSerialNo}", examProfileSerialNo);

        Request request = new Request.Builder()
                .url(getNextJobEndpoint)
                .get()
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                logger.info("[SignAndIssue] CertID: {} Get next Cert INFO ID success.",responseBody);
                return Long.parseLong(responseBody);
            } else {
                System.err.println(getNextJobEndpoint +" Request failed with status code: " + response.code());
                return null;
            }
        }
    }

    public void startScheduleSignAndIssue(String examProfileSerialNo, String jwtTokenFromFrontEnd) throws IOException {
        String startScheduleEndpoint = startScheduleSigningEndpoint.replace("{examProfileSerialNo}", examProfileSerialNo);

        Request request = new Request.Builder()
                .url(startScheduleEndpoint)
                .put(RequestBody.create(new byte[0])) // Empty PUT request body
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Response: " + response.body().string());
            } else {
                System.err.println(startScheduleEndpoint+" Request failed with status code: " + response.code());
            }
        }
    }

    public void uploadSignedPdf(Long certId, String jwtTokenFromFrontEnd, byte [] signedPdfByteArray) {
        // Define the endpoint for uploading signed PDF
        String uploadSignedPdfEndpoint = uploadSignedCertEndpoint.replace("{certInfoId}", certId.toString());

        System.out.println("Uploading signed PDF to: " + uploadSignedPdfEndpoint);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "",
                        RequestBody.create(signedPdfByteArray,MediaType.parse("application/pdf")))
                .build();

        Request request = new Request.Builder()
                .url(uploadSignedPdfEndpoint)
                .post(requestBody)
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Response: " + response.body().string());
            } else {
                System.err.println(uploadSignedPdfEndpoint+" Request failed with status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public List<String> downloadCertPdf(Long certId,String jwtTokenFromFrontEnd) {

        String fileDownloadUrl = downloadCertEndpoint.replace("{certInfoId}", certId.toString());

        Request request = new Request.Builder()
                .url(fileDownloadUrl)
                .post(RequestBody.create(new byte[0]))
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Define your static temp path
            Path tempDir = Paths.get(certDownloadTempPath);
            // Create the directory if it doesn't exist
            File directory = tempDir.toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create a temporary file in the static temp path

            // Unzip and save contents to temp directory
            try (ResponseBody responseBody = response.body();
                InputStream inputStream = responseBody.byteStream()) {
                List<String> savedLocation = unzip(inputStream, tempDir.toAbsolutePath().toString());
                System.out.println("Saved location: " + savedLocation);

                return savedLocation;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
*/
/*
    private List<String> unzip(InputStream zipInputStream, String destDir) throws IOException {
        List<String> savedLocation = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = newFile(new File(destDir), zipEntry);
                savedLocation.add(newFile.getAbsolutePath());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // Write file content
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        return savedLocation;
    }
*/

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public byte[] prepareEproofPdfForSigning(String jwtTokenFromFrontEnd,Long certId, String unsignedJson, String signedValue, String publicKey) {
        // Define the endpoint for uploading signed PDF
        String prepareEproofPdfUrl2 = prepareEproofPdfUrl.replace("{certInfoId}", certId.toString());

        System.out.println("Preparing EProof PDF: " + prepareEproofPdfUrl2);

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("eproofDataJson", unsignedJson);
        jsonData.addProperty("signedProofValue", signedValue);
        jsonData.addProperty("publicKey", publicKey);

        // Create a RequestBody with the JSON data
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData.toString());

        Request request = new Request.Builder()
                .url(prepareEproofPdfUrl2)
                .post(requestBody)
                .addHeader("Authorization", jwtTokenFromFrontEnd)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Prepare and download PDF successful." );
                return response.body().bytes();
            } else {
                System.err.println(prepareEproofPdfUrl2 + " Request failed with status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] prepareEproofPdfForSigningForReissueCert(String jwt, Long certInfoRenewId, String unsignedJson, String signedValue, String publicKey) {
        // Define the endpoint for uploading signed PDF
        String prepareEproofPdfUrl2 = prepareReissueCertEproofPdfUrl.replace("{certInfoRenewId}", certInfoRenewId.toString());

        System.out.println("Preparing EProof PDF: " + prepareEproofPdfUrl2);

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("eproofDataJson", unsignedJson);
        jsonData.addProperty("signedProofValue", signedValue);
        jsonData.addProperty("publicKey", publicKey);


        // Create a RequestBody with the JSON data
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData.toString());

        Request request = new Request.Builder()
                .url(prepareEproofPdfUrl2)
                .post(requestBody)
                .addHeader("Authorization", jwt)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Prepare and download PDF successful." );
                return response.body().bytes();
            } else {
                System.err.println(prepareEproofPdfUrl2 + " Request failed with status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void uploadSignedPdfForReissueCert(Long certInfoRenewId, String jwt, byte[] signedPdf) throws IOException {
        // Define the endpoint for uploading signed PDF
        String uploadSignedPdfEndpoint = uploadSignedReissueCertEndpoint.replace("{certInfoRenewId}", certInfoRenewId.toString());



        System.out.println("Uploading signed PDF to: " + uploadSignedPdfEndpoint);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "",
                        RequestBody.create(signedPdf,MediaType.parse("application/pdf")))
                .build();

        Request request = new Request.Builder()
                .url(uploadSignedPdfEndpoint)
                .post(requestBody)
                .addHeader("Authorization", jwt)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Response: " + response.body().string());
            } else {
                System.err.println(uploadSignedPdfEndpoint + " Request failed with status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
