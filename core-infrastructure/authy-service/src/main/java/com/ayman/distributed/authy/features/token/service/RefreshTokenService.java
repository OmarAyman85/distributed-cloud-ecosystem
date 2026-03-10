package com.ayman.distributed.authy.features.token.service;

import com.ayman.distributed.authy.common.exception.TokenRefreshException;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.token.model.RefreshToken;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.token.repository.TokenRepository;
import com.ayman.distributed.authy.features.identity.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing refresh tokens lifecycle including creation, validation, and revocation.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration:604800000}") // Default 7 days in ms
    private long refreshTokenDurationMs;

    private final TokenRepository tokenRepository;
    private final ApplicationService applicationService;

    /**
     * Finds a refresh token by its token string.
     * @param token The token string to search for.
     * @return Optional containing the token if found.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    /**
     * Creates a new refresh token for a user and application.
     * @param user The user for whom to create the token.
     * @param application The application context for the token.
     * @return The newly created and persisted refresh token.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, Application application) {
        RefreshToken token = new RefreshToken(user, application, refreshTokenDurationMs / 1000);
        return tokenRepository.save(token);
    }

    /**
     * Revokes a specific refresh token.
     */
    @Transactional
    public void revokeToken(RefreshToken token) {
        token.revoke();
        tokenRepository.save(token);
    }

    /**
     * Marks a refresh token as used.
     */
    @Transactional
    public void markUsed(RefreshToken token) {
        token.markUsed();
        tokenRepository.save(token);
    }

    /**
     * Revokes all active refresh tokens for a specific user.
     * Typically used during logout or password change.
     * @param user The user whose tokens should be revoked.
     */
    @Transactional
    public void revokeAllUserRefreshTokens(User user) {
        List<RefreshToken> activeTokens = tokenRepository.findAllActiveTokensByUserId(user.getId());
        activeTokens.forEach(this::revokeToken);
    }

    /**
     * Verifies that a refresh token has not expired.
     * If expired, deletes the token and throws an exception.
     * @param token The token to verify.
     * @return The same token if valid.
     * @throws TokenRefreshException if the token has expired.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(Instant.now())) {
            tokenRepository.delete(token);
            throw new TokenRefreshException(
                    "Refresh token has expired. Please sign in again."
            );
        }
        return token;
    }

    /**
     * Gets the refresh token duration in seconds.
     * @return The duration in seconds.
     */
    public long getRefreshTokenDurationSeconds() {
        return refreshTokenDurationMs / 1000;
    }
}