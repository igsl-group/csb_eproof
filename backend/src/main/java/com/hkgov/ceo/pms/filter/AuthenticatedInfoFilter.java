package com.hkgov.ceo.pms.filter;

import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import com.hkgov.ceo.pms.service.UserService;
import com.hkgov.ceo.pms.validator.UserValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

import static com.hkgov.ceo.pms.config.Constants.ANONYMOUS_USER;

public class AuthenticatedInfoFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatedInfoFilter.class);
    private final AuthenticatedInfoService authenticatedInfoService;
    private final UserService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserValidator userValidator;

    public AuthenticatedInfoFilter(AuthenticatedInfoService authenticatedInfoService, UserService userService, HandlerExceptionResolver handlerExceptionResolver, UserValidator userValidator) {
        this.authenticatedInfoService = authenticatedInfoService;
        this.userService = userService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userValidator = userValidator;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String currentUserLoginId = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .orElse(null);

        try {
            if (currentUserLoginId != null && !ANONYMOUS_USER.equals(currentUserLoginId)) {
                User currentUser = userService.getUserByLoginId(currentUserLoginId);
                userValidator.validate(currentUser);
                authenticatedInfoService.setCurrentUser(currentUser);
            }
            logger.debug("Current currentUserLoginId: {}", currentUserLoginId);
            filterChain.doFilter(request, response);
        } catch (GenericException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
