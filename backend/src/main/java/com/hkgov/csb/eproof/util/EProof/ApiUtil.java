package com.hkgov.csb.eproof.util.EProof;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hkgov.csb.eproof.exception.GenericException;
import io.micrometer.common.util.StringUtils;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hkgov.csb.eproof.util.CommonUtil.gson;
import static org.eclipse.persistence.annotations.Convert.JSON;


public class ApiUtil {
	private static final Logger logger = LogManager.getLogger(ApiUtil.class);
	private static HttpUtil httpUtil;

	static {
		try {
			httpUtil = new HttpUtil();
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	private static String generateRandomSaltValue() {
		int length = 16;
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{};':\"\\|,.<>/?~`";
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < length; i++) {
			result.append(characters.charAt((int) (Math.random() * characters.length())));
		}
		return result.toString();
	}

//	public static Map<String, Object> requestSalt() throws Exception {
//
//		Response ret = null;
//		String url = "http://example.com/hkicSalt"; // Replace with actual API URL
//
//		List<String> headersArray = new ArrayList<String>();
//		headersArray.add("Content-Type");
//		headersArray.add("application/json");
//		String[] headers = headersArray.toArray(new String[0]);
//
//		HashMap jsonMap = new HashMap();
//		jsonMap.put("value", generateRandomSaltValue());
//		jsonMap.put("description", "Reasonable description for the salt");
//
//		String jsonRequestBody = gson.toJson(jsonMap);
//		Response response = httpUtil.post(url, headers, jsonRequestBody);
//
//		logger.debug("POST /hkicSalt Request Body");
//		logger.debug(jsonRequestBody);
//
//		ret = response;
//		if (response.code() != 200) {
//			logger.error("http return code ["+response.code()+"] " + response.body().string());
//		}
//		Map<String, Object> result = new HashMap<>();
//
//		try (Response httpResponse = client.newCall(request).execute()) {
//			if (!httpResponse.isSuccessful()) {
//				throw new IOException("Unexpected code " + httpResponse);
//			}
//
//			JSONObject jsonResponse = new JSONObject(httpResponse.body().string());
//
//			String status = jsonResponse.getString("status");
//			String message = jsonResponse.getString("message");
//
//			if ("Successful".equals(status)) {
//				JSONObject data = jsonResponse.getJSONObject("data");
//				result.put("status", status);
//				result.put("message", message);
//				result.put("id", data.getString("id"));
//				result.put("value", data.getString("value"));
//				result.put("description", data.getString("description"));
//			} else {
//				throw new GenericException("API Error", message);
//			}
//		}
//		return result;
//	}

	public static Boolean getAccessTokenByClientCredentials(EProofConfigProperties config) throws Exception {
		// check if the token is valid
		Boolean ret = false;
		// 20240903update get new token no matter if it is empty
		if ( config.getAccessToken() == null || config.getAccessToken().length()== 0 ) {
//		if ( true ) {
			logger.debug("Empty token, try to get one");

			String url = config.getUrl() +  "/auth/token";

			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			String[] headers = headersArray.toArray(new String[0]);

			HashMap jsonMap = new HashMap();
			jsonMap.put("grantType", "clientCredentials");
			jsonMap.put("clientId", config.getClientId());
			jsonMap.put("clientSecret", config.getClientSecret());

			String jsonRequestBody = gson.toJson(jsonMap);

			Response response = httpUtil.post(url, headers, jsonRequestBody);

			logger.info("getToken-response.statusCode: " + response.code());
			logger.info("getToken-response.body: " + response.body().string());

			if (response.code() == 200) {
				JsonElement jelement = new JsonParser().parse(response.body().string());
				JsonObject jobject = jelement.getAsJsonObject();
				//accessToken = jobject.getAsJsonObject("data").get("accessToken").getAsString();
				config.setAccessToken(jobject.getAsJsonObject("data").get("accessToken").getAsString());
				ret = true;
			} else {
				config.setAccessToken("");
			}

		}else{
			logger.info("Already a valid token");
			ret = true;
		}
		return ret;
	}

	public static Response addSigner(EProofConfigProperties config, String keyName, String publicKeyCert) throws Exception {
		Response ret = null;
		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/didDoc";

			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			requestBodyJsonMap.put("publicKeyCert", publicKeyCert);
			requestBodyJsonMap.put("keyName", keyName);
			//requestBodyJsonMap.put("pdfUrl", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("expiryDate"));

			//Request Headers
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			String jsonRequestBody = gson.toJson(requestBodyJsonMap);
			logger.debug("PUT /didDoc Request Body");
			logger.debug(jsonRequestBody);

			Response response = httpUtil.put(url, headers, jsonRequestBody);
			ret = response;
			//retrieve response body outside. it can only be retrieved once
			// if (response.code() != 200) {
			//	logger.error("http return code ["+response.code()+"] " + response.body().string());
			//}
		}else{
			logger.error("Invalid Access Token");
		}
		return ret;
	}

	public static Response shareEProof(EProofConfigProperties config, String uuid, int version, LocalDateTime expirationDate, int qrCodeMaxCount, String eproof) throws Exception {
		Response ret = null;
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +   "/sharedEProof";

			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			requestBodyJsonMap.put("id", uuid);
			requestBodyJsonMap.put("version", version);
			requestBodyJsonMap.put("accessExpiryDate",
					(expirationDate == null) ? "9999-12-31T00:00:00Z" : expirationDate.format(formatter));
			requestBodyJsonMap.put("maxAccessCount", qrCodeMaxCount);
			requestBodyJsonMap.put("eProof", eproof);

			//Request Headers
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			String jsonRequestBody = gson.toJson(requestBodyJsonMap);
			logger.debug("POST /sharedEProof Request Body");
			logger.debug(jsonRequestBody);

			Response response = httpUtil.post(url, headers, jsonRequestBody);
			ret = response;
			if (response.code() != 200) {
				logger.error("http return code ["+response.code()+"] " + response.body().string());
			}
		}else{
			logger.error("Invalid Access Token");
		}
		return ret;
	}

	public static Response registerEproof(String uuid, EProofConfigProperties config, String eproofId, String eproofTypeId, String templateCode,
										  String expiryDate, String issuranceDate, String dataHash, int downloadMaxCount, String downloadExpiryDate, String formattedPublishDate,
										  String hkidHash, String saltUuid, String sdidHash
	) throws Exception {
		Response ret= null;

		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/eProofMetadata";



			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			String authMethod = "01";
			String walletOption = "01";

			if (StringUtils.isNotEmpty(hkidHash) && StringUtils.isNotEmpty(saltUuid) && StringUtils.isNotEmpty(sdidHash)) {
				authMethod = "03";
				walletOption = "03";
				requestBodyJsonMap.put("hkicHash", hkidHash);
				requestBodyJsonMap.put("hkicSaltId", saltUuid);
				requestBodyJsonMap.put("sdid", sdidHash);
			}

			if(uuid != null)
				requestBodyJsonMap.put("id", uuid);
			requestBodyJsonMap.put("eProofId", eproofId);
			requestBodyJsonMap.put("eProofTypeId", eproofTypeId);
			requestBodyJsonMap.put("templateCode", templateCode);
//			if (expiryDate != null)
			requestBodyJsonMap.put("expiryDate", "9999-12-31T00:00:00Z");
			requestBodyJsonMap.put("issuanceDate",  issuranceDate);
			requestBodyJsonMap.put("dataHash", dataHash);
			requestBodyJsonMap.put("authMethod", authMethod);
			requestBodyJsonMap.put("dataUrl", config.getDataUrl());
			requestBodyJsonMap.put("otpUrl", config.getOtpUrl());
			// TODO
			requestBodyJsonMap.put("publishDate", formattedPublishDate);
			requestBodyJsonMap.put("walletOption", walletOption);
			requestBodyJsonMap.put("allowDownloadPdf", true);
			requestBodyJsonMap.put("downloadMaxCount", String.valueOf(downloadMaxCount));
			if(downloadExpiryDate != null)
				requestBodyJsonMap.put("downloadExpiryDate", "9999-12-31T00:00:00Z");

			//Request Headers
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			String jsonRequestBody = gson.toJson(requestBodyJsonMap);
			logger.info("POST /eProofMetadata Request Body");
			logger.info(jsonRequestBody);

			Response response = httpUtil.post(url, headers, jsonRequestBody);
			ret = response;
			//if (response.code() != 200) {
			//	logger.error("http return code ["+response.code()+"] " + response.body().string());
			//}
		}else{
			logger.error("Invalid Access Token");
		}
		return ret;
	}

	public static Response issueEproofAddPDF(EProofConfigProperties config, String uuid) throws Exception {
		Response ret = null;
		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/eProofMetadata";

			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			requestBodyJsonMap.put("id", uuid);
			requestBodyJsonMap.put("pdfUrl", config.getPdfUrl());
			requestBodyJsonMap.remove("qrCode");

			//Request Headers
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			String jsonRequestBody = gson.toJson(requestBodyJsonMap);
			logger.info("PUT /eProofMetadata Request Body");
			logger.info(jsonRequestBody);

			Response response = httpUtil.put(url, headers, jsonRequestBody);
			ret = response;
			//retrieve response body outside. it can only be retrieved once
			// if (response.code() != 200) {
			//	logger.error("http return code ["+response.code()+"] " + response.body().string());
			//}
		}else{
			logger.error("Invalid Access Token");
		}
		return ret;
	}

	public static Response issueEproofUpdatePDFHash(EProofConfigProperties config, String uuid, String pdfHash) throws Exception {
		Response ret = null;
		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/eProofMetadata/pdfHash";

			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			requestBodyJsonMap.put("id", uuid);
			requestBodyJsonMap.put("pdfHash", pdfHash);
			//requestBodyJsonMap.put("pdfUrl", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("expiryDate"));

			//Request Headers
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			String jsonRequestBody = gson.toJson(requestBodyJsonMap);
			logger.info("PUT /eProofMetadata/pdfHash Request Body");
			logger.info(jsonRequestBody);

			Response response = httpUtil.put(url, headers, jsonRequestBody);
			ret = response;
			//retrieve response body outside. it can only be retrieved once
			//if (response.code() != 200) {
			//	logger.error("http return code ["+response.code()+"] " + response.body().string());
			//}
		}else{
			logger.error("Invalid Access Token");
		}
		return ret;
	}

	public static Response getEProof(EProofConfigProperties config, String uuid) throws Exception {
		//JSONObject ret = null;
		Response ret = null;

		//Check if need to get the correct token
		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/eProofMetadata/" + uuid;
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			Response response = httpUtil.get(url, headers);
			ret = response;
		}else{
			logger.debug("Cannot get correct token");
		}
		return ret;
	}

	public static Response revocation(EProofConfigProperties config, String uuid, boolean isRevoked, boolean isWithdrawn) throws Exception {
		Response ret = null;
		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/eProofMetadata/revocation";

			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			requestBodyJsonMap.put("id", uuid);
			if(isRevoked)
				requestBodyJsonMap.put("isRevoked", true);
			if(isWithdrawn)
				requestBodyJsonMap.put("isWithdrawn", true);

			//Request Headers
			List<String> headersArray = new ArrayList<String>();
			headersArray.add("Content-Type");
			headersArray.add("application/json");
			headersArray.add("Authorization");
			headersArray.add("Bearer " + config.getAccessToken());
			String[] headers = headersArray.toArray(new String[0]);

			String jsonRequestBody = gson.toJson(requestBodyJsonMap);
			logger.debug("PUT /revocation Request Body");
			logger.debug(jsonRequestBody);

			Response response = httpUtil.put(url, headers, jsonRequestBody);
			ret = response;
		}else{
			logger.error("Invalid Access Token");
		}
		return ret;
	}
}
