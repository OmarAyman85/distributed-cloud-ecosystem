package com.ayman.distributed.simuclothing.repository;

import com.ayman.distributed.simuclothing.model.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findByType(String type);
}
