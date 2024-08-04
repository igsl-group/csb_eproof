package com.hkgov.csb.eproof.service;


import com.hkgov.csb.eproof.entity.UserSession;

public interface UserSessionService {

    UserSession persistUserSession(String dpUserId, String dpDeptId);

    void removeSessionJwt(String jwt);

    void updateSessionJwt(Long userSessionId,String jwt);

}
