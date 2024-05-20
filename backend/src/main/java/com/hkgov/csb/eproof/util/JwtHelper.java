package com.hkgov.csb.eproof.util;



import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.JwtConfigurationProperties;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.exception.GenericException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
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


@Component
public class JwtHelper {

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






    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Map<String, Object> extraClaims, Date expirationDate) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void verifyToken(String token, User user, UserSession userSession) {
        if (user == null || userSession == null) {
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }
        boolean loginIdMatch = extractUsername(token).equals(user.getUsername());
        if (!loginIdMatch) {
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }
        boolean tokenMatch = StringUtils.equals(userSession.getJwt(), token);
        if (!allowMultipleLogin && !tokenMatch) {
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }
        //boolean tokenNotExpired = userSession.getTimeStamp().plusSeconds(jwtConfigurationProperties.getExpirationSeconds()).isAfter(LocalDateTime.now());
        boolean tokenNotExpired = userSession.getLastActiveTime().plusDays(30).isAfter(LocalDateTime.now());
        if (!tokenNotExpired) {
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }
    }

   /* public void verifyToken(String token, DsLoginOneTimePassword dsLoginOneTimePassword, UserSession userSession) {
        if (dsLoginOneTimePassword == null || userSession == null) {
            throw new CommonException(JWT_TOKEN_INVALID_EXCEPTION_CODE, JWT_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
        boolean loginIdMatch = extractUsername(token).equals(dsLoginOneTimePassword.getUsername());
        if (!loginIdMatch) {
            throw new CommonException(JWT_TOKEN_INVALID_EXCEPTION_CODE, JWT_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
        boolean tokenMatch = StringUtils.equals(userSession.getToken(), token);
        if (!allowMultipleLogin && !tokenMatch) {
            throw new CommonException(JWT_TOKEN_REPLACED_EXCEPTION_CODE, JWT_TOKEN_INVALID_EXCEPTION_MESSAGE);
        }
        boolean tokenNotExpired = dsLoginOneTimePassword.getExpiryDate().isAfter(LocalDateTime.now());
        if (!tokenNotExpired) {
            throw new CommonException(JWT_TOKEN_EXPIRY_EXCEPTION_CODE, JWT_TOKEN_EXPIRY_EXCEPTION_MESSAGE);
        }
    }*/


    public String extractClaimWithKey(String token, String key){
        return this.extractClaimWithKey(token,key,String.class);
    }
    public <T> T extractClaimWithKey(String token, String key, Class<T> valueDataType){
        Claims claims = extractAllClaims(token);
        return claims.get(key,valueDataType);
    }
/*    public boolean isTokenValid(String token, User user, UserSession userSession) {
        if (user == null || userSession == null) {
            return false;
        }
        boolean loginIdMatch = extractUsername(token).equals(user.getUsername());
        boolean tokenMatch = StringUtils.equals(userSession.getToken(), token);
        boolean tokenNotExpired = userSession.getTimeStamp().plusSeconds(jwtConfigurationProperties.getExpirationSeconds()).isAfter(LocalDateTime.now());
        return loginIdMatch && tokenMatch && tokenNotExpired;
    }*/

    public String getJwtFromRequest(HttpServletRequest request) {

        for (Cookie cookie : request.getCookies()) {
            if(Constants.COOKIE_KEY_ACCESS_TOKEN.equals(cookie.getName())){
                return cookie.getValue();
            }
        }

        // jwt cannot get from cookies
        // try to get from header
        String jwtHeader = request.getHeader(jwtConfigurationProperties.getAuthorizationHeader());
        if(isJwtHeaderValid(jwtHeader)){
            return this.extractJwtFromHeader(jwtHeader);
        }

        // jwt at this moment should not be empty
        // if its still empty, means it cannot being retrieved from either header or cookies.
        return null;
    }

    public boolean nonJwtHeader(String authorizationHeader) {
        return StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(jwtConfigurationProperties.getTokenPrefix());
    }

    public boolean isJwtHeaderValid(String jwtHeader){
        return StringUtils.isNotBlank(jwtHeader) && jwtHeader.startsWith(jwtConfigurationProperties.getTokenPrefix());
    }

    public String extractJwtFromHeader(String authorizationHeader) {
        return authorizationHeader.replace(jwtConfigurationProperties.getTokenPrefix(), StringUtils.EMPTY);
    }


    public boolean isTokenExpired(String token) {
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
