package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.service.AuthenticatedInfoService;

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
