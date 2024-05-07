package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.User;

public interface AuthenticatedInfoService {

    User getCurrentUser();

    void setCurrentUser(User currentUser);
}
