package com.ayman.distributed.authy.features.token.repository;

import com.ayman.distributed.authy.features.token.model.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    
    /**
     * Finds a revoked token record by its JTI.
     */
    Optional<RevokedToken> findByJti(String jti);

    /**
     * Deletes all revoked token records that have technically expired anyway.
     * Used for database maintenance.
     */
    void deleteByExpiresAtBefore(Instant now);
}
