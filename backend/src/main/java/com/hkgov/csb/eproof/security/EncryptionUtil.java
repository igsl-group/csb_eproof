package com.hkgov.csb.eproof.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Component
public class EncryptionUtil {
    
    private static final String ALGORITHM = "AES";

    final Environment environment;

    private static String key;

//    private static String decodedKey;
    private static SecretKeySpec keySpec ;

    public EncryptionUtil(Environment environment){
//        EncryptionUtil.decodedKey =  new String(Base64.getDecoder().decode(base64EncodedKey), StandardCharsets.UTF_8);
        this.environment = environment;
        EncryptionUtil.key = environment.getProperty("aes-encrypt-key");
        EncryptionUtil.keySpec  = new SecretKeySpec(key.getBytes(), "AES");
    }


    /*public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // AES-256
        return keyGen.generateKey();
    }*/

    public static String encrypt(String originalString) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(originalString.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedString) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

/*
    public static SecretKey getKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
*/
}