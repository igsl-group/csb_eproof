package com.hkgov.csb.eproof.util.EProof;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.KeyManagementException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hkgov.csb.eproof.util.CommonUtil.gson;


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
	public static Boolean getAccessTokenByClientCredentials(EProofConfigProperties config) throws Exception {
		// check if the token is valid
		Boolean ret = false;
		if ( config.getAccessToken() == null || config.getAccessToken().length()== 0 ) {
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
			logger.info("getToken-response.body: " + response.body());

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
			logger.debug("Already a valid token");
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
					(expirationDate == null) ? "9999-12-31T23:59:59Z" : expirationDate.format(formatter));
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
										  String expiryDate, String issuranceDate, String dataHash, int downloadMaxCount, String downloadExpiryDate) throws Exception {
		Response ret= null;

		if (ApiUtil.getAccessTokenByClientCredentials(config)) {
			String url = config.getUrl() +  "/eProofMetadata";

			//Request Body
			HashMap requestBodyJsonMap = new HashMap<>();
			if(uuid != null)
				requestBodyJsonMap.put("id", uuid);
			requestBodyJsonMap.put("eProofId", eproofId);
			requestBodyJsonMap.put("eProofTypeId", eproofTypeId);
			requestBodyJsonMap.put("templateCode", templateCode);
//			if (expiryDate != null)
			requestBodyJsonMap.put("expiryDate", "9999-12-31T09:46:33.000Z");
			requestBodyJsonMap.put("issuanceDate",  issuranceDate);
			requestBodyJsonMap.put("dataHash", dataHash);
			requestBodyJsonMap.put("authMethod", "01");
			requestBodyJsonMap.put("dataUrl", config.getDataUrl());
			requestBodyJsonMap.put("otpUrl", config.getOtpUrl());
			requestBodyJsonMap.put("downloadMaxCount", String.valueOf(downloadMaxCount));
			if(downloadExpiryDate != null)
				requestBodyJsonMap.put("downloadExpiryDate", "9999-12-31T09:46:33.000Z");

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
