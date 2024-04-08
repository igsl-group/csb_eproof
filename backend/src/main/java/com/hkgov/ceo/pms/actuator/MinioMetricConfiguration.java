package com.hkgov.ceo.pms.actuator;


import com.hkgov.ceo.pms.config.MinioConfiguration;
import com.hkgov.ceo.pms.config.MinioConfigurationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Aspect
@Configuration
@ConditionalOnClass({MinioClient.class, ManagementContextAutoConfiguration.class})
@ConditionalOnEnabledHealthIndicator("minio")
@AutoConfigureBefore(HealthContributorAutoConfiguration.class)
@AutoConfigureAfter(MinioConfiguration.class)
public class MinioMetricConfiguration {

    private static final String OPERATION = "operation";
    private static final String STATUS = "status";
    private static final String BUCKET = "bucket";
    private static final String OK = "ok";
    private static final String KO = "ko";
    private static final String LIST_OBJECTS = "listObjects";
    private static final String GET_OBJECT = "getObject";
    private static final String PUT_OBJECT = "putObject";
    private static final String REMOVE_OBJECT = "removeObject";
    private static final String LIST_BUCKETS = "listBuckets";
    private static final String LIST_BUCKET = ".list.bucket";
    private final MeterRegistry meterRegistry;
    private final MinioConfigurationProperties minioConfigurationProperties;
    private Timer listOkTimer;
    private Timer listKoTimer;
    private Timer getOkTimer;
    private Timer getKoTimer;
    private Timer putOkTimer;
    private Timer putKoTimer;
    private Timer removeOkTimer;
    private Timer removeKoTimer;
    private Timer listBucketOkTimer;
    private Timer listBucketKoTimer;

    @Autowired
    public MinioMetricConfiguration(MeterRegistry meterRegistry, MinioConfigurationProperties minioConfigurationProperties) {
        this.meterRegistry = meterRegistry;
        this.minioConfigurationProperties = minioConfigurationProperties;
    }

    @PostConstruct
    public void initTimers() {
        listOkTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, LIST_OBJECTS)
                .tag(STATUS, OK)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        listKoTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, LIST_OBJECTS)
                .tag(STATUS, KO)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        getOkTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, GET_OBJECT)
                .tag(STATUS, OK)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        getKoTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, GET_OBJECT)
                .tag(STATUS, KO)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        putOkTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, PUT_OBJECT)
                .tag(STATUS, OK)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        putKoTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, PUT_OBJECT)
                .tag(STATUS, KO)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        listBucketOkTimer = Timer
                .builder(minioConfigurationProperties.getMetricName() + LIST_BUCKET)
                .tag(OPERATION, LIST_BUCKETS)
                .tag(STATUS, OK)
                .register(meterRegistry);

        listBucketKoTimer = Timer
                .builder(minioConfigurationProperties.getMetricName() + LIST_BUCKET)
                .tag(OPERATION, LIST_BUCKETS)
                .tag(STATUS, KO)
                .register(meterRegistry);

        removeOkTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, REMOVE_OBJECT)
                .tag(STATUS, OK)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);

        removeKoTimer = Timer
                .builder(minioConfigurationProperties.getMetricName())
                .tag(OPERATION, REMOVE_OBJECT)
                .tag(STATUS, KO)
                .tag(BUCKET, minioConfigurationProperties.getBucket())
                .register(meterRegistry);
    }


    @ConditionalOnBean(MinioClient.class)
    @Around("execution(* io.minio.MinioClient.getObject(..))")
    public Object getMeter(ProceedingJoinPoint pjp) throws Throwable {
        return getReturnObject(pjp, getOkTimer, getKoTimer);
    }

    @ConditionalOnBean(MinioClient.class)
    @Around("execution(* io.minio.MinioClient.listObjects(..))")
    public Object listMeter(ProceedingJoinPoint pjp) throws Throwable {
        return getReturnObject(pjp, listOkTimer, listKoTimer);
    }

    @ConditionalOnBean(MinioClient.class)
    @Around("execution(* io.minio.MinioClient.putObject(..))")
    public Object putMeter(ProceedingJoinPoint pjp) throws Throwable {
        return getReturnObject(pjp, putOkTimer, putKoTimer);
    }

    @ConditionalOnBean(MinioClient.class)
    @Around("execution(* io.minio.MinioClient.listBuckets(..))")
    public Object listBucketMeter(ProceedingJoinPoint pjp) throws Throwable {
        return getReturnObject(pjp, listBucketOkTimer, listBucketKoTimer);
    }

    @ConditionalOnBean(MinioClient.class)
    @Around("execution(* io.minio.MinioClient.removeObject(..))")
    public Object removeMeter(ProceedingJoinPoint pjp) throws Throwable {
        return getReturnObject(pjp, removeOkTimer, removeKoTimer);
    }

    private Object getReturnObject(ProceedingJoinPoint pjp, Timer getOkTimer, Timer getKoTimer) throws Throwable {
        long l = System.currentTimeMillis();

        try {
            Object proceed = pjp.proceed();
            getOkTimer.record(System.currentTimeMillis() - l, TimeUnit.MILLISECONDS);
            return proceed;
        } catch (Exception e) {
            getKoTimer.record(System.currentTimeMillis() - l, TimeUnit.MILLISECONDS);
            throw e;
        }
    }
}
