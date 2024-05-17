package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.entity.UserSession;

import java.util.List;


public interface JwtService {

    String generateToken(String dpUserId, String dpDeptId, UserSession userSession);

}
