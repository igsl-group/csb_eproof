package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSessionService {

    UserSession newSession(User user);

    UserSession getSession(long sessionId);

    UserSession updateSessionTime(long sessionId);

    UserSession deleteSession(long sessionId);

    Page<UserSession> search(Pageable pageable, String keyword);

    Page<UserSession> getSessions(Pageable pageable);

    void updateSessionToken(UserSession userSession, String jwtToken);

    void cleanupIdleUserSessions();
}
