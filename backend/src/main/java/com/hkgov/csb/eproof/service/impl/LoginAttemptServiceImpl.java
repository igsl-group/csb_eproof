package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.ConfigurationRepository;
import com.hkgov.csb.eproof.entity.Configuration;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.exception.LoginException;
import com.hkgov.csb.eproof.service.LoginAttemptService;
import com.hkgov.csb.eproof.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.hkgov.csb.eproof.config.ConfigurationConstants.LOGIN_MAX_ATTEMPTS;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.ACCOUNT_BLOCKED_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.ACCOUNT_BLOCKED_EXCEPTION_MESSAGE;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final UserService userService;
    private final ConfigurationRepository configurationRepository;

    public LoginAttemptServiceImpl(UserService userService, ConfigurationRepository configurationRepository) {
        this.userService = userService;
        this.configurationRepository = configurationRepository;
    }


    @Transactional
    @Override
    public void loginSucceeded(String id) {
        User user = userService.getUserByLoginId(id);
        user.setLastLoginDate(LocalDateTime.now());
        user.setLoginAttempt(0);
    }

    @Override
    public void loginFailed(String id) {
        int maxLoginAttempt = Optional.ofNullable(configurationRepository.findByCode(LOGIN_MAX_ATTEMPTS))
                .map(Configuration::getValue)
                .map(Integer::parseInt)
                .orElse(3);
        userService.increaseLoginAttempt(id);
        if (userService.getUserByLoginId(id).getLoginAttempt() >= maxLoginAttempt) {
            userService.blockUser(id);
            throw new LoginException(ACCOUNT_BLOCKED_EXCEPTION_CODE, ACCOUNT_BLOCKED_EXCEPTION_MESSAGE);
        }
    }
}
