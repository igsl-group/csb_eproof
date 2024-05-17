/*
package com.hkgov.csb.eproof.filter;

import com.hkgov.csb.eproof.servlet.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.hkgov.csb.eproof.constants.Constants.START_TIME;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

public class ContentCachingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ContentCachingFilter.class);

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("In ContentCachingFilter: method = [{}] path = [{}]", request.getMethod(), request.getRequestURI());
        }
        if (shouldCache(request)) {
            request = new CachedBodyHttpServletRequest(request);
        }

        request.setAttribute(START_TIME, startTime);
        filterChain.doFilter(request, response);

    }

    private boolean shouldCache(HttpServletRequest request) {
        return Optional.ofNullable(request.getContentType())
                .map(contentType -> !contentType.contains(MULTIPART_FORM_DATA))
                .orElse(false);
    }

}
*/
