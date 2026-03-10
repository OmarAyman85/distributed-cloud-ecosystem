package com.ayman.distributed.simuclothing.repository;

import com.ayman.distributed.simuclothing.model.FashionProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FashionProfileRepository extends JpaRepository<FashionProfile, UUID> {
    Optional<FashionProfile> findByUserId(String userId);
}
