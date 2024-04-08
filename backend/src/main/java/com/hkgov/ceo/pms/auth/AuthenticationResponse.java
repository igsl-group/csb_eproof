package com.hkgov.ceo.pms.auth;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private String token;

    public String getToken() {
        return token;
    }

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public AuthenticationResponse() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class Builder {

        private String token;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponse build() {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(this.token);
            return response;
        }
    }
}
