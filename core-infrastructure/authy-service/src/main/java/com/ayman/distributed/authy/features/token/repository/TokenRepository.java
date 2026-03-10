package com.ayman.distributed.authy.features.token.repository;

import com.ayman.distributed.authy.features.token.model.RefreshToken;
import com.ayman.distributed.authy.features.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Used for the User ID parameter

/**
 * Repository interface for managing {@link RefreshToken} entities.
 * Provides methods to retrieve valid tokens and find tokens by their value.
 */
@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Retrieves all non-revoked and non-used tokens for a given user.
     * This query mirrors the core validity logic of the RefreshToken entity.
     *
     * @param userId The ID of the user whose valid tokens are to be retrieved (Long).
     * @return A list of active {@link RefreshToken} entities.
     */
    @Query("""
            SELECT t FROM RefreshToken t 
            WHERE t.user.id = :userId 
            AND t.revoked = false 
            AND t.used = false
            """)
    List<RefreshToken> findAllActiveTokensByUserId(@Param("userId") Long userId);

    /**
     * Finds a token by its unique token string.
     *
     * @param token The token string.
     * @return An {@link Optional} containing the token if found.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all tokens associated with a user, typically during explicit logout or account deactivation.
     * Returns the number of entities deleted.
     *
     * @param user The user whose tokens should be deleted.
     * @return The count of tokens deleted.
     */
    int deleteByUser(User user);
}