package com.hkgov.ceo.pms.auth;

import com.hkgov.ceo.pms.audit.core.annotation.Audit;
import com.hkgov.ceo.pms.security.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtHelper jwtHelper;

    public AuthenticationController(AuthenticationService authenticationService, JwtHelper jwtHelper) {
        this.authenticationService = authenticationService;
        this.jwtHelper = jwtHelper;
    }

    @Audit(action = "Login", resourceWording = "[Login ID]: ",
            resourceResolverName = "authenticationAuditResourceResolver")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Audit(action = "Logout",
            resourceResolverName = "voidAuditResourceResolver")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        long currentSessionId = jwtHelper.getSessionIdFromHttpRequest(request);
        authenticationService.logout(currentSessionId);
    }

}
