package com.hkgov.csb.eproof.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AuditConfig {

    @Value("${server.application.code:CSB_Eproof}")
    private String applicationCode;

    @Bean
    public String applicationCode() {
        return applicationCode;
    }

}
