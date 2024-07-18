package hk.gov.fsd.eis.esign.Util;

import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class CommonUtil
{
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

	public static String readFileAsString(String file)throws Exception
	{
		Path path = Paths.get(file);
		String return_str = new String(Files.readAllBytes(path));

		//return new String(Files.readAllBytes(path));
		return return_str;
	}
}
