package com.hkgov.ceo.pms.auth;

import com.hkgov.ceo.pms.dao.UserRepository;
import com.hkgov.ceo.pms.entity.UserSession;
import com.hkgov.ceo.pms.security.JwtHelper;
import com.hkgov.ceo.pms.service.UserSessionService;
import com.hkgov.ceo.pms.validator.UserValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;
    private final UserSessionService userSessionService;
    private final UserValidator userValidator;

    public AuthenticationService(UserRepository userRepository, JwtHelper jwtHelper, AuthenticationManager authenticationManager, UserSessionService userSessionService, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.jwtHelper = jwtHelper;
        this.authenticationManager = authenticationManager;
        this.userSessionService = userSessionService;
        this.userValidator = userValidator;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() ->  new UsernameNotFoundException("User not found"));
        userValidator.validate(user);
        UserSession userSession = userSessionService.newSession(user);
        var jwtToken = jwtHelper.generateTokenWithSessionId(user, userSession.getUserSessionId());
        userSessionService.updateSessionToken(userSession, jwtToken);
        return new AuthenticationResponse.Builder()
                .token(jwtToken)
                .build();
    }

    public void logout(long sessionId) {
        userSessionService.deleteSession(sessionId);
    }
}
