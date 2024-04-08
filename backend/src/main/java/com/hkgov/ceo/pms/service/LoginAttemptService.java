package com.hkgov.ceo.pms.service;

public interface LoginAttemptService {

    void loginSucceeded(String id);

    void loginFailed(String id);
}
