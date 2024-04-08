package com.hkgov.ceo.pms.service;

import com.hkgov.ceo.pms.entity.User;

public interface AuthenticatedInfoService {

    User getCurrentUser();

    void setCurrentUser(User currentUser);
}
