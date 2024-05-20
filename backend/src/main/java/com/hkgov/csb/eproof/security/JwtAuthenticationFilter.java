
package com.hkgov.csb.eproof.security;

import com.hkgov.csb.eproof.constants.*;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.dao.UserSessionRepository;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.UserSessionService;
import com.hkgov.csb.eproof.util.JwtHelper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final UserRepository userRepository;

    private final UserSessionRepository userSessionRepository;

    @Value("${security.whitelist}")
    private String[] authWhitelist;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    public JwtAuthenticationFilter(JwtHelper jwtHelper, HandlerExceptionResolver handlerExceptionResolver, UserRepository userRepository, UserSessionRepository userSessionRepository) {
        this.jwtHelper = jwtHelper;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI().replace(contextPath,"");
        return Stream.of(authWhitelist).anyMatch(x -> new AntPathMatcher().match(x, requestUri));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String jwt = jwtHelper.getJwtFromRequest(request);
        final String dpUserId;

        try {
            dpUserId = jwtHelper.extractUsername(jwt);
            if (dpUserId != null && isAuthenticationContextEmpty()) {
                Long sessionId = jwtHelper.extractClaimWithKey(jwt, Constants.JWT_KEY_SESSIONID,Long.class);
                User user = userRepository.getUserBydpUserId(dpUserId);
                UserSession userSession = userSessionRepository.findById(sessionId).orElse(null);
                jwtHelper.verifyToken(jwt, user, userSession);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException | GenericException ex) {
            ex.printStackTrace();
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticationContextEmpty() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
}

