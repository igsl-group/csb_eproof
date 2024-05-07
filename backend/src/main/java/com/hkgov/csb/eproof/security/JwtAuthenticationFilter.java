package com.hkgov.csb.eproof.security;

import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.UserService;
import com.hkgov.csb.eproof.service.UserSessionService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(JwtHelper jwtHelper, UserService userService, UserSessionService userSessionService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtHelper = jwtHelper;
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = jwtHelper.getAuthorizationHeader(request);
        final String jwt;
        final String loginId;
        if (jwtHelper.nonJwtHeader(authorizationHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = jwtHelper.extractJwtFromHeader(authorizationHeader);
        try {
            loginId = jwtHelper.extractUsername(jwt);
            if (loginId != null && isAuthenticationContextEmpty()) {
                long sessionId = jwtHelper.getSessionId(jwt);
                User user = userService.getUserByLoginId(loginId);
                UserSession userSession = userSessionService.getSession(sessionId);
                jwtHelper.verifyToken(jwt, user, userSession);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException | GenericException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticationContextEmpty() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
