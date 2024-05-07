package com.hkgov.csb.eproof.security;

import com.hkgov.csb.eproof.exception.LoginException;
import com.hkgov.csb.eproof.service.LoginAttemptService;
import com.hkgov.csb.eproof.exception.ExceptionConstants;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        if (event.getException() instanceof BadCredentialsException) {
            loginAttemptService.loginFailed(event.getAuthentication().getName());
            throw new LoginException(ExceptionConstants.USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_CODE, ExceptionConstants.USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_MESSAGE);
        }
    }
}
