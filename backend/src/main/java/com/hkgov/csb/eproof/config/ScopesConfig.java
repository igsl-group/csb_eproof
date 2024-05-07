package com.hkgov.csb.eproof.config;

import com.hkgov.csb.eproof.service.AuthenticatedInfoService;
import com.hkgov.csb.eproof.service.impl.AuthenticatedInfoServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ScopesConfig {

    @Bean
    @RequestScope
    public AuthenticatedInfoService AuthenticatedInfoService() {
        return new AuthenticatedInfoServiceImpl();
    }
}
