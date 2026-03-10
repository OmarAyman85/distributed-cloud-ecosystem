package com.ayman.distributed.media.features.storage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalStorageProviderTest {

    private LocalStorageProvider localStorageProvider;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        localStorageProvider = new LocalStorageProvider();
        ReflectionTestUtils.setField(localStorageProvider, "uploadPath", tempDir.toString());
    }

    @Test
    void upload_ShouldSaveFileLocally() {
        String content = "Hello World";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String key = "test.txt";
        String contentType = "text/plain";

        String url = localStorageProvider.upload(inputStream, key, contentType);

        assertNotNull(url);
        assertTrue(url.contains(key));
        
        File file = tempDir.resolve(key).toFile();
        assertTrue(file.exists());
    }

    @Test
    void delete_ShouldRemoveFile() throws Exception {
        String key = "delete-me.txt";
        Path filePath = tempDir.resolve(key);
        java.nio.file.Files.createFile(filePath);
        
        assertTrue(filePath.toFile().exists());

        localStorageProvider.delete(key);

        assertFalse(filePath.toFile().exists());
    }
}
