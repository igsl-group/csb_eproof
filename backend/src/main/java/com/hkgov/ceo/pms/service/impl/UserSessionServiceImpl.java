package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.audit.common.web.ClientInfoHolder;
import com.hkgov.ceo.pms.config.JwtConfigurationProperties;
import com.hkgov.ceo.pms.dao.UserSessionRepository;
import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserSession;
import com.hkgov.ceo.pms.exception.GenericException;
import com.hkgov.ceo.pms.service.UserSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.hkgov.ceo.pms.exception.ExceptionConstants.USER_SESSION_NOT_FOUND_EXCEPTION_CODE;
import static com.hkgov.ceo.pms.exception.ExceptionConstants.USER_SESSION_NOT_FOUND_EXCEPTION_MESSAGE;

@Transactional
@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final JwtConfigurationProperties jwtConfigurationProperties;

    @Value("${login.allowMultipleLogin:false}")
    private boolean allowMultipleLogin;

    public UserSessionServiceImpl(UserSessionRepository userSessionRepository, JwtConfigurationProperties jwtConfigurationProperties) {
        this.userSessionRepository = userSessionRepository;
        this.jwtConfigurationProperties = jwtConfigurationProperties;
    }

    @Override
    public UserSession newSession(User user) {
        if (!allowMultipleLogin) {
            userSessionRepository.deleteAllByUser(user);
        }
        LocalDateTime now = LocalDateTime.now();
        var newSession = new UserSession();
        newSession.setUser(user);
        newSession.setClientIpAddress(getCurrentSessionIpAddress());
        newSession.setCreateDate(now);
        newSession.setTimeStamp(now);
        return userSessionRepository.save(newSession);
    }

    @Override
    public UserSession getSession(long sessionId) {
        return getSessionOptional(sessionId)
                .orElse(null);
    }

    @Override
    public UserSession updateSessionTime(long sessionId) {
        return getSessionOptional(sessionId)
                .map(session -> {
                    session.setClientIpAddress(getCurrentSessionIpAddress());
                    session.setTimeStamp(LocalDateTime.now());
                    return userSessionRepository.save(session);
                })
                .orElseThrow(() -> new GenericException(USER_SESSION_NOT_FOUND_EXCEPTION_CODE, USER_SESSION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public UserSession deleteSession(long sessionId) {
        return getSessionOptional(sessionId)
                .map(session -> {
                    userSessionRepository.delete(session);
                    return session;
                }).orElseThrow(() ->
                        new GenericException(USER_SESSION_NOT_FOUND_EXCEPTION_CODE, USER_SESSION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public Page<UserSession> search(Pageable pageable, String keyword) {
        return userSessionRepository.findByUser(jwtConfigurationProperties.getExpirationSeconds(), pageable, keyword);
    }

    @Override
    public Page<UserSession> getSessions(Pageable pageable) {
        return userSessionRepository.findAllValidUserSession(jwtConfigurationProperties.getExpirationSeconds(), pageable);
    }

    @Override
    public void updateSessionToken(UserSession userSession, String jwtToken) {
        userSession.setToken(jwtToken);
        userSessionRepository.save(userSession);
    }

    @Override
    public void cleanupIdleUserSessions() {
        userSessionRepository.deleteAllExpiredUserSession(jwtConfigurationProperties.getExpirationSeconds());
    }

    private Optional<UserSession> getSessionOptional(long sessionId) {
        return userSessionRepository.findById(sessionId);
    }

    private String getCurrentSessionIpAddress() {
        final var clientInfo = ClientInfoHolder.getClientInfo();
        return clientInfo.getClientIpAddress();
    }
}
