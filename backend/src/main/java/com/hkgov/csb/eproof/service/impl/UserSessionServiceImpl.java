package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.dao.UserSessionRepository;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.service.UserSessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class UserSessionServiceImpl implements UserSessionService {
    private AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    public UserSessionServiceImpl(UserRepository userRepository,
                                  UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public UserSession persistUserSession(String dpUserId, String dpDeptId) {
        User user = userRepository.getUserByDpUserIdAndDpDeptId(dpUserId,dpDeptId);
        if(user == null){
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }
        UserSession userSession = userSessionRepository.getUserSessionByUserId(user.getId());
        if (userSession == null){
            userSession = this.createUserSession(user,null);
        }
        return userSession;
    }

    private UserSession createUserSession(User user,String jwt){
        UserSession us = new UserSession();
        us.setUser(user);
        us.setJwt(jwt);
        us.setLastActiveTime(LocalDateTime.now());
        us.setCreatedDate(LocalDateTime.now());
        // TODO: Add client info holder
        us.setClientIpAddress("");
        userSessionRepository.save(us);
        return us;
    }
    public void updateSessionJwt(Long userSessionId,String jwt){
        UserSession userSession = userSessionRepository.findById(userSessionId).get();
        userSession.setJwt(jwt);
        userSessionRepository.save(userSession);
    }

    public void removeSessionJwt(String jwt){
        UserSession userSession = userSessionRepository.getUserSessionByJwt(jwt);
//        auditLogService.addLog("Create","Create User" +user.getUsername(), request);
        userSessionRepository.delete(userSession);
    }
}
