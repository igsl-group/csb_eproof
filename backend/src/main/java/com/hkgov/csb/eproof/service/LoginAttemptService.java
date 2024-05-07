package com.hkgov.csb.eproof.service;

public interface LoginAttemptService {

    void loginSucceeded(String id);

    void loginFailed(String id);
}
