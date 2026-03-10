package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.features.auth.dto.AuthenticationResponse;
import com.ayman.distributed.authy.features.auth.model.MagicLinkToken;
import com.ayman.distributed.authy.features.auth.repository.MagicLinkRepository;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.identity.service.ApplicationService;
import com.ayman.distributed.authy.features.token.model.RefreshToken;
import com.ayman.distributed.authy.features.token.service.JwtService;
import com.ayman.distributed.authy.features.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling Magic Link (passwordless) authentication.
 */
@Service
@RequiredArgsConstructor
public class MagicLinkService {

    private static final Logger log = LoggerFactory.getLogger(MagicLinkService.class);
    
    // Magic link expires in 15 minutes
    private static final int EXPIRATION_MINUTES = 15;

    private final MagicLinkRepository magicLinkRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationService applicationService;

    /**
     * Generates a magic link for the given email.
     * In a real app, this would send an email. Here we log the URL.
     */
    @Transactional
    public void sendMagicLink(String email, String appKey) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        // Revoke any previous magic links for this user
        // Note: deleteByUser requires a custom method or manual deletion
        // For simplicity, we just create a new one. In production, cleanup is recommended.

        String token = UUID.randomUUID().toString();
        MagicLinkToken magicLinkToken = MagicLinkToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                .used(false)
                .build();

        magicLinkRepository.save(magicLinkToken);

        // Simulate sending email
        String magicLinkUrl = String.format("http://localhost:8080/api/magic-link/verify?token=%s&appKey=%s", token, appKey);
        log.info(">>> MAGIC LINK GENERATED for [{}]: {}", email, magicLinkUrl);
        System.out.println(">>> MAGIC LINK GENERATED for [" + email + "]: " + magicLinkUrl);
    }

    /**
     * Verifies the magic link token and returns authentication tokens if valid.
     */
    @Transactional
    public AuthenticationResponse verifyMagicLink(String token, String appKey) {
        MagicLinkToken magicLinkToken = magicLinkRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired magic link token"));

        if (magicLinkToken.isUsed()) {
            throw new IllegalArgumentException("Magic link has already been used");
        }

        if (magicLinkToken.isExpired()) {
            throw new IllegalArgumentException("Magic link has expired");
        }

        // Mark token as used
        magicLinkToken.setUsed(true);
        magicLinkRepository.save(magicLinkToken);

        User user = magicLinkToken.getUser();
        Application app = applicationService.findByAppKeyOrDefault(appKey);
        List<String> appRoles = applicationService.getAppRolesForUser(user, app.getAppKey());

        // Standard token generation logic
        String accessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
        refreshTokenService.revokeAllUserRefreshTokens(user);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, app);

        return new AuthenticationResponse(accessToken, refreshTokenEntity.getToken(), user.getUsername());
    }
}
