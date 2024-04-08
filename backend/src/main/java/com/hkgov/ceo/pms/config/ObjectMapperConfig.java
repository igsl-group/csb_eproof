package com.hkgov.ceo.pms.config;

import com.hkgov.ceo.pms.util.ObjectMapperUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapperUtil objectMapper() {
        return new ObjectMapperUtil();
    }
}
