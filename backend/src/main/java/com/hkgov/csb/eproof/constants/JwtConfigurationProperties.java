package com.hkgov.csb.eproof.constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
@Getter
@NoArgsConstructor
public class JwtConfigurationProperties {
    private String secretKey;
    private String tokenPrefix;
    private String authorizationHeader;
    private Integer expirationSeconds;

/*    public JwtConfigurationProperties() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public Integer getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(Integer expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }*/
}
