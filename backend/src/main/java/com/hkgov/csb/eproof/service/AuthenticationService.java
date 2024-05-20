package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.PermissionDto;
import com.hkgov.csb.eproof.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;



public interface AuthenticationService {

    void authenticate(String dpUserId, String dpDeptId);

    User getCurrentUser();

}
