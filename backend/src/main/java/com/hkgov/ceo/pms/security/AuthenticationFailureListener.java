package com.hkgov.ceo.pms.security;

import com.hkgov.ceo.pms.exception.LoginException;
import com.hkgov.ceo.pms.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_MESSAGE;

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
            throw new LoginException(USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_CODE, USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_MESSAGE);
        }
    }
}
