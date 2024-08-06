package com.hkgov.csb.localSigning.config;


import com.hkgov.csb.localSigning.configProperties.ThreadPoolConfigProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties({ThreadPoolConfigProps.class})
@EnableAsync
public class ThreadPoolConfig {

    private final ThreadPoolConfigProps threadPoolConfigProps;

    public ThreadPoolConfig(ThreadPoolConfigProps threadPoolConfigProps) {
        this.threadPoolConfigProps = threadPoolConfigProps;
    }


    @Bean(name = "normalThreadPool")
    public Executor normalThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolConfigProps.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolConfigProps.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolConfigProps.getQueueCapacity());
        executor.setThreadNamePrefix(threadPoolConfigProps.getNamePrefix());
        executor.initialize();
        return  executor;
    }

}
