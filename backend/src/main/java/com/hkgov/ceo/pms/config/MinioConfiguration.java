package com.hkgov.ceo.pms.config;

import com.hkgov.ceo.pms.exception.GenericException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinioConfigurationProperties.class)
@ComponentScan("com.hkgov.ceo.pms")
public class MinioConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioConfiguration.class);
    private final MinioConfigurationProperties minioConfigurationProperties;

    public MinioConfiguration(MinioConfigurationProperties minioConfigurationProperties) {
        this.minioConfigurationProperties = minioConfigurationProperties;
    }

    @Bean
    public MinioClient minioClient() throws IOException, InvalidKeyException, NoSuchAlgorithmException, MinioException {

        MinioClient minioClient;
        if (!configuredProxy()) {
            minioClient = MinioClient.builder()
                    .endpoint(minioConfigurationProperties.getUrl())
                    .credentials(minioConfigurationProperties.getAccessKey(), minioConfigurationProperties.getSecretKey())
                    .build();
        } else {
            minioClient = MinioClient.builder()
                    .endpoint(minioConfigurationProperties.getUrl())
                    .credentials(minioConfigurationProperties.getAccessKey(), minioConfigurationProperties.getSecretKey())
                    .httpClient(client())
                    .build();
        }
        minioClient.setTimeout(
                minioConfigurationProperties.getConnectTimeout().toMillis(),
                minioConfigurationProperties.getWriteTimeout().toMillis(),
                minioConfigurationProperties.getReadTimeout().toMillis()
        );

        if (minioConfigurationProperties.isCheckBucket()) {
            LOGGER.debug("Checking if bucket {} exists", minioConfigurationProperties.getBucket());
            BucketExistsArgs existsArgs = BucketExistsArgs.builder()
                    .bucket(minioConfigurationProperties.getBucket())
                    .build();
            boolean b = minioClient.bucketExists(existsArgs);
            if (!b) {
                if (minioConfigurationProperties.isCreateBucket()) {
                    try {
                        MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                                .bucket(minioConfigurationProperties.getBucket())
                                .build();
                        minioClient.makeBucket(makeBucketArgs);
                    } catch (Exception e) {
                        throw new GenericException("cannot.create.bucket", "Cannot create bucket", e);
                    }
                } else {
                    throw new IllegalStateException("Bucket does not exist: " + minioConfigurationProperties.getBucket());
                }
            }
        }

        return minioClient;
    }

    private boolean configuredProxy() {
        String httpHost = System.getProperty("http.proxyHost");
        String httpPort = System.getProperty("http.proxyPort");
        return httpHost != null && httpPort != null;
    }

    private OkHttpClient client() {
        String httpHost = System.getProperty("http.proxyHost");
        String httpPort = System.getProperty("http.proxyPort");

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (httpHost != null)
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpHost, Integer.parseInt(httpPort))));
        return builder
                .build();
    }

}
