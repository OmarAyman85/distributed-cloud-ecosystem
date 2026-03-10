package com.ayman.distributed.media.common.config;

import com.ayman.distributed.media.features.storage.service.StorageProvider;
import com.ayman.distributed.media.features.storage.service.impl.LocalStorageProvider;
import com.ayman.distributed.media.features.storage.service.impl.S3StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfiguration {

    @Value("${storage.provider:LOCAL}")
    private String storageProvider;

    @Value("${storage.s3.region:us-east-1}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    @Primary
    public StorageProvider storageProvider(LocalStorageProvider localProvider, S3StorageProvider s3Provider) {
        if ("S3".equalsIgnoreCase(storageProvider)) {
            return s3Provider;
        }
        return localProvider;
    }
}
