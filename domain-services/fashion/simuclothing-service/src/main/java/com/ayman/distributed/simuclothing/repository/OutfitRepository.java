package com.ayman.distributed.simuclothing.repository;

import com.ayman.distributed.simuclothing.model.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByUserId(String userId);
}
