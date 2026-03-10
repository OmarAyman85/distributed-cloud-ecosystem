package com.ayman.distributed.media.features.storage.service.impl;

import com.ayman.distributed.media.features.storage.service.StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
public class S3StorageProvider implements StorageProvider {

    private final S3Client s3Client;
    
    @Value("${storage.s3.bucket}")
    private String bucketName;
    
    @Value("${storage.s3.region}")
    private String region;

    public S3StorageProvider(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String upload(InputStream inputStream, String key, String contentType) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            
            return getURL(key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public String getURL(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }
}
