package com.ayman.distributed.media.features.storage.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "media_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, unique = true)
    private String storageKey; // The path/filename in S3 or local disk

    private String contentType;

    private Long size;

    @Column(nullable = false)
    private String url; // Publicly accessible URL

    @Column(nullable = false)
    private String provider; // LOCAL or S3

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
