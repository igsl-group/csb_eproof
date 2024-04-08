package com.hkgov.ceo.pms.validator;

import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.exception.LoginException;
import com.hkgov.ceo.pms.service.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static com.hkgov.ceo.pms.config.Constants.BLOCKED;
import static com.hkgov.ceo.pms.config.Constants.DISABLED;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.ACCOUNT_BLOCKED_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.ACCOUNT_BLOCKED_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.ACCOUNT_DISABLED_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.ACCOUNT_DISABLED_EXCEPTION_MESSAGE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.PASSWORD_EXPIRE_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.PASSWORD_EXPIRE_EXCEPTION_MESSAGE;

@Component
public class UserValidator {
    private final UserService userService;


    private static final Map<String, Pair<String, String>> USER_STATUS_ERROR_MAP = Map.of(
            DISABLED, Pair.of(ACCOUNT_DISABLED_EXCEPTION_CODE, ACCOUNT_DISABLED_EXCEPTION_MESSAGE),
            BLOCKED, Pair.of(ACCOUNT_BLOCKED_EXCEPTION_CODE, ACCOUNT_BLOCKED_EXCEPTION_MESSAGE)
    );

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    public void validate(User user) {
        Optional.ofNullable(user.getStatus())
                .map(USER_STATUS_ERROR_MAP::get)
                .ifPresent(pair -> {
                    throw new LoginException(pair.getLeft(), pair.getRight());
                });
        if (userService.isUserPasswordExpire(user)) {
            throw new LoginException(PASSWORD_EXPIRE_EXCEPTION_CODE, PASSWORD_EXPIRE_EXCEPTION_MESSAGE);
        }
    }
}
