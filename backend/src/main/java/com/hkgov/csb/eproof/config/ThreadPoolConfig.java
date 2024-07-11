package com.hkgov.csb.eproof.config;


import com.hkgov.csb.eproof.config.config_properties.ScheduledTaskThreadPoolConfigProps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties({ScheduledTaskThreadPoolConfigProps.class})
public class ThreadPoolConfig {

    private final ScheduledTaskThreadPoolConfigProps scheduledTaskThreadPoolConfigProps;

    public ThreadPoolConfig(ScheduledTaskThreadPoolConfigProps scheduledTaskThreadPoolConfigProps) {
        this.scheduledTaskThreadPoolConfigProps = scheduledTaskThreadPoolConfigProps;
    }

    @Bean(name = "scheduledTaskThreadPool")
    public Executor scheduledTaskThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(scheduledTaskThreadPoolConfigProps.getCorePoolSize());
        executor.setMaxPoolSize(scheduledTaskThreadPoolConfigProps.getMaxPoolSize());
        executor.setQueueCapacity(scheduledTaskThreadPoolConfigProps.getQueueCapacity());
        executor.setThreadNamePrefix(scheduledTaskThreadPoolConfigProps.getThreadNamePrefix());
        executor.initialize();
        return  executor;
    }

}
