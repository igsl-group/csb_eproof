package com.hkgov.csb.eproof.util;


import com.hkgov.csb.eproof.config.RequestLogFilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@Component
public final class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private final RequestLogFilterConfig requestLogFilterConfig;
    private static final List<RequestMatcher> ignoreRequestMatchers = new ArrayList<>();
  public HttpUtils(RequestLogFilterConfig requestLogFilterConfig) {
      this.requestLogFilterConfig = requestLogFilterConfig;
  }
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"

            // you can add more matching headers here ...
    };

   /* public HttpUtils(RequestLogFilterConfig requestLogFilterConfig) {
        this.requestLogFilterConfig = requestLogFilterConfig;
        requestLogFilterConfig.getIgnoredPaths()
                .forEach(pattern -> ignoreRequestMatchers.add(new AntPathRequestMatcher(pattern)));
    }*/

   /* public static String getRequestIP(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }*/

    public static String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getRemoteAddr();
    }

    public static String getUserAgent() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("User-Agent");
    }
    public static String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return currentUserName;
    }

    public static Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

    public static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }

    public static String getRequestBody(HttpServletRequest request) {
        if (shouldSkip(request)) {
            return request.getContentType();
        }
        String payload = null;

        try {
            InputStream inputStream = request.getInputStream();
            byte[] buf = StreamUtils.copyToByteArray(inputStream);
            if (buf.length > 0) {
                try {
                    int maxLength = buf.length;
                    payload = new String(buf, 0, maxLength, request.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    logger.warn("UnsupportedEncoding.", e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read request body.", e);
        }
        return payload;
    }

    private static boolean shouldSkip(HttpServletRequest request) {
        return isMultipartFormData(request) || matchIgnoreUrlPatterns(request);
    }

    private static boolean matchIgnoreUrlPatterns(HttpServletRequest request) {
        return ignoreRequestMatchers
                .stream()
                .anyMatch(requestMatcher -> requestMatcher.matches(request));
    }

    private static boolean isMultipartFormData(HttpServletRequest request) {
        return Optional.ofNullable(request.getContentType())
                .map(contentType -> contentType.contains(MULTIPART_FORM_DATA))
                .orElse(false);
    }
}
