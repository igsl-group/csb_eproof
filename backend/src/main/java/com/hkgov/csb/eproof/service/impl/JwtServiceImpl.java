package com.hkgov.csb.eproof.service.impl;


import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dao.UserRepository;
import com.hkgov.csb.eproof.entity.User;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.JwtService;
import com.hkgov.csb.eproof.util.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class JwtServiceImpl implements JwtService {

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;

    public JwtServiceImpl(JwtHelper jwtHelper, UserRepository userRepository) {
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
    }

    @Override
    public String generateToken(String dpUserId, String dpDeptId, UserSession userSession) {
        User user = userRepository.getUserByDpUserIdAndDpDeptId(dpUserId,dpDeptId);
        if(user == null){
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }

        Map<String, Object> claimMap = new HashMap<>();
        claimMap.put(Constants.JWT_KEY_USERID,user.getId());
        claimMap.put(Constants.JWT_KEY_USERNAME,user.getUsername());
        claimMap.put(Constants.JWT_KEY_DPUSERID,user.getDpUserId());
        claimMap.put(Constants.JWT_KEY_SESSIONID,userSession.getId());

        String jwt = jwtHelper.generateToken(claimMap,user);

        return jwt;
    }
}




