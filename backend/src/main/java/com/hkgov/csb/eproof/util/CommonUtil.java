package com.hkgov.csb.eproof.util;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class CommonUtil {
	public static Gson gson = new Gson().newBuilder().disableHtmlEscaping().create();
	public static byte[] readBytesFromString(String inputString, MessageDigest digest)  {
		return digest.digest(inputString.getBytes(StandardCharsets.UTF_8));
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
}
