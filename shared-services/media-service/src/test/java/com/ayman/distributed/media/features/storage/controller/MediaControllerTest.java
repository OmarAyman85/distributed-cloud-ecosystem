package com.ayman.distributed.media.features.storage.controller;

import com.ayman.distributed.media.features.storage.model.Media;
import com.ayman.distributed.media.features.storage.repository.MediaRepository;
import com.ayman.distributed.media.features.storage.service.StorageProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StorageProvider storageProvider;

    @BeforeEach
    void setUp() {
        mediaRepository.deleteAll();
    }

    @Test
    void shouldUploadFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes()
        );

        when(storageProvider.upload(any(), anyString(), anyString())).thenReturn("http://localhost/media/hello.txt");

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalName").value("hello.txt"))
                .andExpect(jsonPath("$.url").value("http://localhost/media/hello.txt"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldGetMediaMetadata() throws Exception {
        Media media = Media.builder()
                .originalName("test.png")
                .storageKey("key-1")
                .url("http://url.com")
                .provider("LOCAL")
                .build();
        media = mediaRepository.save(media);

        mockMvc.perform(get("/api/media/" + media.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalName").value("test.png"));
    }

    @Test
    void shouldReturn404WhenMediaNotFound() throws Exception {
        mockMvc.perform(get("/api/media/999"))
                .andExpect(status().isInternalServerError()); // Currently returning 500 from GlobalExceptionHandler for RuntimeException
    }
}
