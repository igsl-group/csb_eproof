package com.hkgov.csb.eproof.auth;

import java.io.Serializable;

public class AuthenticationRequest implements Serializable {

    private String loginId;

    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class Builder {

        private String loginId;
        private String password;

        public Builder loginId(String loginId) {
            this.loginId = loginId;
            return this;
        }

        public AuthenticationRequest build() {
            AuthenticationRequest request = new AuthenticationRequest();
            request.setLoginId(this.loginId);
            request.setPassword(this.password);
            return request;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }
    }

}
