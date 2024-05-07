package com.hkgov.csb.eproof.interceptor;

import com.hkgov.csb.eproof.service.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.hkgov.csb.eproof.config.Constants.START_TIME;


@ControllerAdvice
public class ResponseBodyInterceptor implements ResponseBodyAdvice<Object> {

    private final LoggingService loggingService;

    public ResponseBodyInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public boolean supports(@NotNull MethodParameter returnType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        if (MediaType.APPLICATION_JSON.equals(selectedContentType)) {
            loggingService.logResponse(servletRequest, servletResponse, body, getElapsedTime(servletRequest));
        } else {
            loggingService.logResponse(servletRequest, servletResponse, selectedContentType, getElapsedTime(servletRequest));
        }
        return body;
    }

    private long getElapsedTime(HttpServletRequest request) {
        if (request.getAttribute(START_TIME) == null) {
            return 0;
        }
        return System.currentTimeMillis() - (long) request.getAttribute(START_TIME);
    }
}