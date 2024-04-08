package com.hkgov.ceo.pms.entity;

public class EmailContext {
    private String token;
    private String serverUrl;
    private String passwordExpirationDays;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getPasswordExpirationDays() {
        return passwordExpirationDays;
    }

    public void setPasswordExpirationDays(String passwordExpirationDays) {
        this.passwordExpirationDays = passwordExpirationDays;
    }
}
