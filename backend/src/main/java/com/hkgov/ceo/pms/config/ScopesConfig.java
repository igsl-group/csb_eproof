package com.hkgov.ceo.pms.config;

import com.hkgov.ceo.pms.service.AuthenticatedInfoService;
import com.hkgov.ceo.pms.service.impl.AuthenticatedInfoServiceImpl;
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
