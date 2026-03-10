package com.ayman.distributed.media.features.storage.service;

import com.ayman.distributed.media.features.storage.model.Media;
import com.ayman.distributed.media.features.storage.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final StorageProvider storageProvider;
    private final MediaRepository mediaRepository;

    @Value("${storage.provider:LOCAL}")
    private String currentProvider;

    public Media uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String storageKey = UUID.randomUUID().toString() + extension;

        try {
            String url = storageProvider.upload(file.getInputStream(), storageKey, file.getContentType());

            Media media = Media.builder()
                    .originalName(originalFilename)
                    .storageKey(storageKey)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .url(url)
                    .provider(currentProvider)
                    .build();

            return mediaRepository.save(media);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file upload", e);
        }
    }

    public Media getMedia(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));
    }

    public void deleteMedia(Long id) {
        Media media = getMedia(id);
        storageProvider.delete(media.getStorageKey());
        mediaRepository.delete(media);
    }
}
