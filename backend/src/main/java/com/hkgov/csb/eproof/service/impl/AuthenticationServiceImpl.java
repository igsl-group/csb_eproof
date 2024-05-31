package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.AuthenticationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void authenticate(String dpUserId, String dpDeptId) {
        User user = userRepository.getUserByDpUserIdAndDpDeptId(dpUserId,dpDeptId);

        if(user == null){
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }

        if (!Constants.STATUS_ACTIVE.equals(user.getStatus())){
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }


    }

    @Override
    public User getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication() != null? (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal():null;
    }
}
