package com.metarash.backend.config;

import com.metarash.backend.config.props.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(properties.url())
                    .credentials(properties.accessKey(), properties.secretKey())
                    .build();

            boolean isBucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.bucketName()).build());

            if (!isBucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(properties.bucketName()).build());
            }

            log.info("MinIO client initialized successfully for url: {}", properties.url());
            return client;

        } catch (Exception e) {
            log.error("Failed to initialize MinIO client: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO initialization failed", e);
        }
    }
}