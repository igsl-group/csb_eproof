package hk.gov.ogcio.eproof.controller;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public final class CommonUtil {
    private static final Logger logger = LogManager.getLogger(CommonUtil.class);

    public static Gson gson = new Gson().newBuilder().disableHtmlEscaping().create();

    public static String base64Encode(String originalString) {
        return base64Encode(originalString.getBytes());
    }

    public static String base64Encode(byte[] bytes) {

        // create base simple encoder object
        Base64.Encoder simpleEncoder = Base64.getEncoder();

        // Encoding string using simple encode
        String encodedString = simpleEncoder.encodeToString(bytes);
        //logger.debug("Encoded string : " + encodedString);

        return encodedString;

    }

    public static String base64Decode(String encodedString) {
        // Create base simple decoder  object
        Base64.Decoder simpleDecoder = Base64.getDecoder();

        // Decoding the encoded string using decoder
        String decodedString = new String(simpleDecoder.decode(encodedString.getBytes()));
        //logger.debug("Decoded String : " + decodedString);
        return decodedString;
    }

    public static String readFileAsString(String file)throws Exception
    {


        logger.debug("File Path : " + file);
        Path path = Paths.get(file);
        String return_str = new String(Files.readAllBytes(path));

        //return new String(Files.readAllBytes(path));
        return return_str;
    }

    public static String readFileAsString(URI file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public static byte[] readBytesFromFile(File file, MessageDigest digest) throws Exception {
        byte[] buffer = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {

            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return digest.digest();
        }
    }

    public static byte[] readBytesFromString(String inputString, MessageDigest digest)  {

            return digest.digest(inputString.getBytes(StandardCharsets.UTF_8));

    }

    public static String signatureWithPrivateKey(String inputString, String privateKeyPath) throws Exception {
        String strPk = readFileAsString(privateKeyPath);
        //String realPK = strPk.replaceAll("-----END PRIVATE KEY-----", "")
        //        .replaceAll("-----BEGIN PRIVATE KEY-----", "")
        //        .replaceAll("\n", "");

        //Louis Revised
        String realPK = strPk.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("[\r\n]", "");

        byte[] b1 = Base64.getDecoder().decode(realPK);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b1);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(inputString.getBytes(StandardCharsets.UTF_8));
        byte[] sigValue = signature.sign();
        String sigValueBase64 =  Base64.getEncoder().encodeToString(sigValue);

        return sigValueBase64;


        //return null;
    }


    public static boolean verifySignatureWithPublicKey(String inputString, String publicKeyPath, String signatureBase64) throws Exception {
        String strPk = readFileAsString(publicKeyPath);

        //String realPK = strPk.replaceAll("-----END PUBLIC KEY-----", "")
        //        .replaceAll("-----BEGIN PUBLIC KEY-----", "")
        //        .replaceAll("\n", "");

        //Louis Revised
        String realPK = strPk.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("[\r\n]", "");

        byte[] b1 = Base64.getDecoder().decode(realPK);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b1);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey =   keyFactory.generatePublic(keySpec);

//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b1);
//
//        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(inputString.getBytes(StandardCharsets.UTF_8));

        byte[] signature= Base64.getDecoder().decode(signatureBase64);
        boolean isCorrect = publicSignature.verify(signature);

        System.out.println("Signature correct: " + isCorrect);

        return isCorrect;

    }

    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    public static Map<String, Object> toMap(JSONObject jsonObject) {
        Map<String, Object> map = new TreeMap<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

}
