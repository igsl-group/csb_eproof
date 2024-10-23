package com.hkgov.csb.eproof.filter;

import com.hkgov.csb.eproof.service.LoggingService;
import com.hkgov.csb.eproof.util.HttpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class RequestLogFilter extends OncePerRequestFilter {
    private final LoggingService loggingService;



    public RequestLogFilter(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        loggingService.logRequest(request, HttpUtils.getRequestBody(request));
        filterChain.doFilter(request, response);
    }

}
