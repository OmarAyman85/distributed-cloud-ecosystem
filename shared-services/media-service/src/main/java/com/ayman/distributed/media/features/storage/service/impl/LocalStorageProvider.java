package com.ayman.distributed.media.features.storage.service.impl;

import com.ayman.distributed.media.features.storage.service.StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageProvider implements StorageProvider {

    @Value("${storage.local.path:./uploads}")
    private String uploadPath;

    @Override
    public String upload(InputStream inputStream, String key, String contentType) {
        try {
            Path directory = Paths.get(uploadPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path targetFile = directory.resolve(key);
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            
            return getURL(key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file locally", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(Paths.get(uploadPath).resolve(key));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete local file", e);
        }
    }

    @Override
    public String getURL(String key) {
        // For local storage, returns a relative path or a local server URL
        return "/media/files/" + key;
    }
}
