package com.hkgov.csb.eproof.config.config_properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool.generate-pdf")
@Getter
@Setter
public class GeneratePdfThreadPoolConfigProps {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private String threadNamePrefix;
}
