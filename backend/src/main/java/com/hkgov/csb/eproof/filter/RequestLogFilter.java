/*
package com.hkgov.csb.eproof.filter;

import com.hkgov.csb.eproof.service.LoggingService;
import com.hkgov.csb.eproof.util.HttpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestLogFilter extends OncePerRequestFilter {
    private final LoggingService loggingService;
    private final HttpUtils httpUtils;


    public RequestLogFilter(LoggingService loggingService, HttpUtils httpUtils) {
        this.loggingService = loggingService;
        this.httpUtils = httpUtils;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        loggingService.logRequest(request, httpUtils.getRequestBody(request));
        filterChain.doFilter(request, response);
    }

}
*/
