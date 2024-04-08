package com.hkgov.ceo.pms.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AuditConfig {

    @Value("${server.application.code:PMS}")
    private String applicationCode;

    @Bean
    public String applicationCode() {
        return applicationCode;
    }

}
