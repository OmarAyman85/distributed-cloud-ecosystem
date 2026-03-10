package com.ayman.distributed.simuclothing.repository;

import com.ayman.distributed.simuclothing.model.WardrobeItem;
import com.ayman.distributed.simuclothing.model.WardrobeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardrobeItemRepository extends JpaRepository<WardrobeItem, Long> {
    List<WardrobeItem> findByUserId(String userId);
    List<WardrobeItem> findByUserIdAndStatus(String userId, WardrobeStatus status);
    Optional<WardrobeItem> findByUserIdAndProductId(String userId, Long productId);
}
