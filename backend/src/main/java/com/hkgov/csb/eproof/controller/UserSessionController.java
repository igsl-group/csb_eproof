package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.audit.core.annotation.Audit;
import com.hkgov.csb.eproof.dto.UserSessionDto;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.mapper.UserSessionMapper;
import com.hkgov.csb.eproof.security.JwtHelper;
import com.hkgov.csb.eproof.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hkgov.csb.eproof.config.AuditTrailConstants.USER_SESSION_WORDING;

@RestController
@RequestMapping("/api/v1/userSession")
public class UserSessionController {
    private final UserSessionService userSessionService;
    private final JwtHelper jwtHelper;

    public UserSessionController(UserSessionService userSessionService, JwtHelper jwtHelper) {
        this.userSessionService = userSessionService;
        this.jwtHelper = jwtHelper;
    }

    @PatchMapping
    public UserSessionDto updateSession(HttpServletRequest request) {
        long currentSessionId = jwtHelper.getSessionIdFromHttpRequest(request);
        return UserSessionMapper.INSTANCE.toDto(userSessionService.updateSessionTime(currentSessionId));
    }

    @Audit(action = "Lockout", resourceWording = USER_SESSION_WORDING, resourceResolverName = "userSessionResourceResolver")
    @Secured({"SESSION_CONTROL_MAINTENANCE"})
    @DeleteMapping
    public UserSessionDto deleteSession(@RequestParam long sessionId) {
        return UserSessionMapper.INSTANCE.toDto(userSessionService.deleteSession(sessionId));
    }

    @Secured({"SESSION_CONTROL_MAINTENANCE"})
    @GetMapping("/search")
    public Page<UserSessionDto> search(HttpServletRequest request,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "createDate") String... properties) {
        long currentSessionId = jwtHelper.getSessionIdFromHttpRequest(request);
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<UserSession> userSessions = userSessionService.search(pageable, keyword);
        return userSessions.map(userSession -> {
            UserSessionDto userSessionDto = UserSessionMapper.INSTANCE.toDto(userSession);
            userSessionDto.setCurrentUserSession(userSession.getUserSessionId() == currentSessionId);
            return userSessionDto;
        });
    }
}
