package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.Constants;
import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.entity.UserSession;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.AuthenticationService;
import com.hkgov.csb.eproof.service.JwtService;
import com.hkgov.csb.eproof.service.UserSessionService;
import com.hkgov.csb.eproof.util.Result;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.scanner.Constant;

@RestController(value = "/auth")
public class AuthenticationController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserSessionService userSessionService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService, UserSessionService userSessionService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userSessionService = userSessionService;
    }


    @GetMapping("/sso")
    public Result sso(@RequestHeader String dpUserId,
                      @RequestHeader String dpDeptId,
            HttpServletRequest req , HttpServletResponse resp){

        /*if(req.getCookies() == null){
            throw new GenericException(ExceptionEnums.ACCESS_DENIED);
        }
        String dpUserId = "",dpDeptId="";

        for(Cookie cookie:req.getCookies()){
            logger.info(cookie.getName()+" : "+cookie.getValue());
            if(Constants.COOKIE_KEY_LOGIN_UID.equals(cookie.getName())){
                dpUserId=cookie.getValue();
            }else if (Constants.COOKIE_KEY_LOGIN_DPDEPTID.equals(cookie.getName())){
                dpDeptId = cookie.getValue();
            }
        }*/

        authenticationService.authenticate(dpUserId,dpDeptId);
        UserSession us = userSessionService.persistUserSession(dpUserId,dpDeptId);
        String token = jwtService.generateToken(dpUserId,dpDeptId,us);
        resp.addCookie(new Cookie(Constants.COOKIE_KEY_ACCESS_TOKEN, token));
        userSessionService.updateSessionJwt(us.getId(),token);

        return Result.success(token);
    }


}
