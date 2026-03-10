package com.ayman.distributed.media.features.storage.controller;

import com.ayman.distributed.media.features.storage.model.Media;
import com.ayman.distributed.media.features.storage.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media Management", description = "Endpoints for uploading and managing files")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file to the configured storage provider")
    public ResponseEntity<Media> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(mediaService.uploadFile(file));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get media metadata by ID")
    public ResponseEntity<Media> getMedia(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMedia(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete media from storage and database")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
}
