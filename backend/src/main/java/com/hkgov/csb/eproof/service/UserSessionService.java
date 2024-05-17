package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.entity.UserSession;

public interface UserSessionService {

    UserSession persistUserSession(String dpUserId, String dpDeptId);
    void updateSessionJwt(Long userSessionId,String jwt);

}
