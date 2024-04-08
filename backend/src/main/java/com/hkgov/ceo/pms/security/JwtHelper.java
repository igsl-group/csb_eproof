package com.hkgov.ceo.pms.security;

import com.hkgov.ceo.pms.config.JwtConfigurationProperties;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserSession;
import com.hkgov.ceo.pms.exception.GenericException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.JWT_TOKEN_EXPIRY_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.JWT_TOKEN_EXPIRY_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.JWT_TOKEN_INVALID_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.JWT_TOKEN_INVALID_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.JWT_TOKEN_REPLACED_EXCEPTION_CODE;

@Component
public class JwtHelper {
    private static final String SID = "sid";

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @Value("${login.allowMultipleLogin:false}")
    private boolean allowMultipleLogin;

    public JwtHelper(JwtConfigurationProperties jwtConfigurationProperties) {
        this.jwtConfigurationProperties = jwtConfigurationProperties;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateTokenWithSessionId(UserDetails userDetails, long sessionId) {
        return generateToken(Map.of(SID, sessionId), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void verifyToken(String token, User user, UserSession userSession) {
        if (user == null || userSession == null) {
            throw new GenericException(JWT_TOKEN_INVALID_EXCEPTION_CODE, JWT_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
        boolean loginIdMatch = extractUsername(token).equals(user.getLoginId());
        if (!loginIdMatch) {
            throw new GenericException(JWT_TOKEN_INVALID_EXCEPTION_CODE, JWT_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
        boolean tokenMatch = StringUtils.equals(userSession.getToken(), token);
        if (!allowMultipleLogin && !tokenMatch) {
            throw new GenericException(JWT_TOKEN_REPLACED_EXCEPTION_CODE, JWT_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
        boolean tokenNotExpired = userSession.getTimeStamp().plusSeconds(jwtConfigurationProperties.getExpirationSeconds()).isAfter(LocalDateTime.now());
        if (!tokenNotExpired) {
            throw new GenericException(JWT_TOKEN_EXPIRY_EXCEPTION_CODE, JWT_TOKEN_EXPIRY_EXCEPTION_MESSAGE);
        }
    }

    public boolean isTokenValid(String token, User user, UserSession userSession) {
        if (user == null || userSession == null) {
            return false;
        }
        boolean loginIdMatch = extractUsername(token).equals(user.getLoginId());
        boolean tokenMatch = StringUtils.equals(userSession.getToken(), token);
        boolean tokenNotExpired = userSession.getTimeStamp().plusSeconds(jwtConfigurationProperties.getExpirationSeconds()).isAfter(LocalDateTime.now());
        return loginIdMatch && tokenMatch && tokenNotExpired;
    }

    public String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(jwtConfigurationProperties.getAuthorizationHeader());
    }

    public boolean nonJwtHeader(String authorizationHeader) {
        return StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(jwtConfigurationProperties.getTokenPrefix());
    }

    public String extractJwtFromHeader(String authorizationHeader) {
        return authorizationHeader.replace(jwtConfigurationProperties.getTokenPrefix(), StringUtils.EMPTY);
    }

    public long getSessionId(String jwt) {
        return extractClaim(jwt, claims -> claims.get(SID, Long.class));
    }

    public long getSessionIdFromHttpRequest(HttpServletRequest request) {
        String authorizationHeader = getAuthorizationHeader(request);
        String jwt = extractJwtFromHeader(authorizationHeader);
        return getSessionId(jwt);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(getSignInKeyString());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getSignInKeyString() {
        return jwtConfigurationProperties.getSecretKey();
    }
}
