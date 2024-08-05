package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.service.AuthenticationService;
import com.hkgov.csb.eproof.service.JwtService;
import com.hkgov.csb.eproof.service.UserSessionService;
import com.hkgov.csb.eproof.util.JwtHelper;
import com.hkgov.csb.eproof.util.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController(value = "/auth")
public class AuthenticationController {
    private final JwtHelper jwtHelper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserSessionService userSessionService;

    public AuthenticationController(JwtHelper jwtHelper, AuthenticationService authenticationService, JwtService jwtService, UserSessionService userSessionService) {
        this.jwtHelper = jwtHelper;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userSessionService = userSessionService;
    }

    @GetMapping("/signOut")
    public Result logout(HttpServletRequest request){
        String jwt = jwtHelper.getJwtFromRequest(request);
        logger.info(jwt);
        userSessionService.removeSessionJwt(jwt);

        return Result.success();
    }

    @GetMapping("/sso")
    public Result sso(
            @RequestHeader(value = "uid", required = false) String uid,
            @RequestHeader(value = "dpdeptid", required = false) String dpDeptId,
            @RequestHeader(value = "host", required = false) String host,
            HttpServletRequest req , HttpServletResponse resp){
        logger.info("uid: {} DeDeptId: {} host: {}", uid, dpDeptId, host);
        logAllRequestHeaders(req);
        authenticationService.authenticate(uid,dpDeptId);
        UserSession us = userSessionService.persistUserSession(uid,dpDeptId);
        String token = jwtService.generateToken(uid,dpDeptId,us);
        resp.addCookie(new Cookie(Constants.COOKIE_KEY_ACCESS_TOKEN, token));
        userSessionService.updateSessionJwt(us.getId(),token);

        return Result.success(token);
    }
    private void logAllRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                logger.info("Header: {} = {}", headerName, headerValue);
            }
        }
    }

}
