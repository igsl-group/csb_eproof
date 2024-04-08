package com.hkgov.ceo.pms.actuator;

import com.hkgov.ceo.pms.config.MinioConfigurationProperties;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Set the Minio health indicator on Actuator.
 *
 */
@ConditionalOnClass(ManagementContextAutoConfiguration.class)
@Component
public class MinioHealthIndicator implements HealthIndicator {

    private static final String BUCKET_NAME = "bucketName";

    @Autowired
    private MinioClient minioClient;
    private final MinioConfigurationProperties minioConfigurationProperties;

    @Autowired
    public MinioHealthIndicator(MinioConfigurationProperties minioConfigurationProperties) {
        this.minioConfigurationProperties = minioConfigurationProperties;
    }


    @Override
    public Health health() {
        if (minioClient == null) {
            return Health.down().build();
        }

        try {
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(minioConfigurationProperties.getBucket())
                    .build();
            if (minioClient.bucketExists(args)) {
                return Health.up()
                        .withDetail(BUCKET_NAME, minioConfigurationProperties.getBucket())
                        .build();
            } else {
                return Health.down()
                        .withDetail(BUCKET_NAME, minioConfigurationProperties.getBucket())
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail(BUCKET_NAME, minioConfigurationProperties.getBucket())
                    .build();
        }
    }
}
