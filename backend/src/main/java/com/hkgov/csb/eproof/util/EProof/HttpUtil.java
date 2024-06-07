package com.hkgov.csb.eproof.util.EProof;

import com.hkgov.csb.eproof.util.SslUtil;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.KeyManagementException;

public class HttpUtil {
	private static final Logger logger = LogManager.getLogger(HttpUtil.class);
	static OkHttpClient okHttpClient;

	public HttpUtil() throws KeyManagementException {
		okHttpClient = SslUtil.getUnsafeOkHttpClient();
	}

	public static Response get(String url, String[] headers) throws Exception {
		Request request = new Request.Builder()
				.url(url).headers(parseHeader(headers)).build();

		Response response = okHttpClient.newCall(request).execute();

		logger.debug("get responseBody: " + response.peekBody(Long.MAX_VALUE).string());
		return response;
	}

	public static Response post(String url, String[] headers, String requestBody) throws Exception {
		Request req = new Request.Builder().url(url).headers(parseHeader(headers)).post(RequestBody.create(MediaType.parse("application/json"), requestBody)).build();

		Response response = okHttpClient.newCall(req).execute();
		logger.debug("post responseBody: " + response.peekBody(Long.MAX_VALUE).string());
		return response;
	}

	public static Response put(String url, String[] headers, String requestBody) throws Exception {

		Request req = new Request.Builder().url(url).headers(parseHeader(headers)).put(RequestBody.create(MediaType.parse("application/json"), requestBody)).build();
		Response response = okHttpClient.newCall(req).execute();

		logger.debug("put responseBody: " + response.peekBody(Long.MAX_VALUE).string());
		return response;
	}


	public static Headers parseHeader(String[] headers){
		if(headers.length %2 ==0){
			Headers.Builder out = new Headers.Builder();
			for (int i = 0; i < headers.length; i += 2) {
				String key = headers[i];
				String value = headers[i + 1];
				out.add(key,value);
			}
			return out.build();
		}else
			return null;

	}
}
