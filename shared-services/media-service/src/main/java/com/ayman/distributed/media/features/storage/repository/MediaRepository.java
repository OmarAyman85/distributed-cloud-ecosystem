package com.ayman.distributed.media.features.storage.repository;

import com.ayman.distributed.media.features.storage.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findByStorageKey(String storageKey);
}
