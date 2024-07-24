package hk.gov.ogcio.eproof.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;


public final class HttpUtil {
    private static final Logger logger = LogManager.getLogger(HttpUtil.class);
    static HttpClient httpClient;

    public HttpUtil() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .build();
    }

    public static HttpResponse get(String url, String[] headers) throws Exception {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url));

        if (headers != null && headers.length % 2 == 0) {
            httpRequestBuilder.headers(headers);
        }
        HttpRequest httpRequest = httpRequestBuilder
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static HttpResponse post(String url, String[] headers, String requestBody) throws Exception {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url));

        if (headers != null && headers.length % 2 == 0) {
            httpRequestBuilder.headers(headers);
        }
        HttpRequest httpRequest = httpRequestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static HttpResponse put(String url, String[] headers, String requestBody) throws Exception {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url));

        if (headers != null && headers.length % 2 == 0) {
            httpRequestBuilder.headers(headers);
        }
        HttpRequest httpRequest = httpRequestBuilder
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }


}
