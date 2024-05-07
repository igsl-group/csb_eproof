package com.hkgov.csb.eproof.config;

import com.hkgov.csb.eproof.util.ObjectMapperUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapperUtil objectMapper() {
        return new ObjectMapperUtil();
    }
}
