package com.ayman.distributed.authy.features.auth.repository;

import com.ayman.distributed.authy.features.auth.model.MagicLinkToken;
import com.ayman.distributed.authy.features.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing MagicLinkToken entities.
 */
@Repository
public interface MagicLinkRepository extends JpaRepository<MagicLinkToken, Long> {

    Optional<MagicLinkToken> findByToken(String token);

    void deleteByUser(User user);
}
