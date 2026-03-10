package com.ayman.distributed.media.features.storage.service;

import com.ayman.distributed.media.features.storage.model.Media;
import com.ayman.distributed.media.features.storage.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private StorageProvider storageProvider;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        // No specific setup needed for basic mocks
    }

    @Test
    void uploadFile_ShouldCoordinateStorageAndRepository() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "some content".getBytes()
        );

        when(storageProvider.upload(any(), anyString(), anyString())).thenReturn("http://s3.aws.com/test.png");
        when(mediaRepository.save(any(Media.class))).thenAnswer(i -> i.getArgument(0));

        Media result = mediaService.uploadFile(file);

        assertNotNull(result);
        assertEquals("test.png", result.getOriginalName());
        assertEquals("http://s3.aws.com/test.png", result.getUrl());
        verify(storageProvider).upload(any(), anyString(), eq("image/png"));
        verify(mediaRepository).save(any(Media.class));
    }

    @Test
    void deleteMedia_ShouldInvokeProviderAndDeleteRecord() {
        Media media = Media.builder()
                .id(1L)
                .storageKey("key-123")
                .build();

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));

        mediaService.deleteMedia(1L);

        verify(storageProvider).delete("key-123");
        verify(mediaRepository).delete(media);
    }
}
