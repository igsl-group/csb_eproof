package com.hkgov.ceo.pms.service.impl;

import com.hkgov.ceo.pms.entity.User;
import com.hkgov.ceo.pms.service.AuthenticatedInfoService;

public class AuthenticatedInfoServiceImpl implements AuthenticatedInfoService {

    private User currentUser;

    public AuthenticatedInfoServiceImpl() {
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
