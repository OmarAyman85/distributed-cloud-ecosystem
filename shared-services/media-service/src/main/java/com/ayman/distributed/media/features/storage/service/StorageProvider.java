package com.ayman.distributed.media.features.storage.service;

import java.io.InputStream;

/**
 * Strategy interface for file storage.
 */
public interface StorageProvider {
    /**
     * Uploads a file to the storage.
     * @param inputStream The file content.
     * @param key The unique storage key (e.g., filename or path).
     * @param contentType The MIME type.
     * @return The public or internal URL of the stored file.
     */
    String upload(InputStream inputStream, String key, String contentType);

    /**
     * Deletes a file from storage.
     * @param key The unique storage key.
     */
    void delete(String key);

    /**
     * Resolves the permanent URL for a storage key.
     * @param key The unique storage key.
     * @return The URL.
     */
    String getURL(String key);
}
