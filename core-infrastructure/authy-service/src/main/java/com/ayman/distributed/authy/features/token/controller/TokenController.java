package com.ayman.distributed.authy.features.token.controller;

import com.ayman.distributed.authy.features.token.dto.TokenIntrospectionResponse;
import com.ayman.distributed.authy.features.token.model.RevokedToken;
import com.ayman.distributed.authy.features.token.repository.RevokedTokenRepository;
import com.ayman.distributed.authy.features.token.service.JwtService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Controller for token-specific operations like introspection and revocation.
 */
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "Token Management", description = "Endpoints for token introspection and revocation")
@Slf4j
public class TokenController {

    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;

    /**
     * OAuth2-style introspection endpoint.
     * Used by API Gateway or other services to validate a token.
     */
    @PostMapping("/introspect")
    @Operation(summary = "Introspect token", description = "Returns status and metadata for a given token (RFC 7662)")
    public ResponseEntity<TokenIntrospectionResponse> introspect(@RequestParam("token") String token) {
        try {
            boolean active = !jwtService.isTokenRevoked(token);
            String username = jwtService.getUsernameFromToken(token);
            String appKey = jwtService.extractAppKey(token);
            List<String> roles = jwtService.extractRoles(token);
            Date expiresAt = jwtService.extractClaim(token, Claims::getExpiration);
            Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

            return ResponseEntity.ok(TokenIntrospectionResponse.builder()
                    .active(active)
                    .subject(username)
                    .appKey(appKey)
                    .roles(roles)
                    .expiresAt(expiresAt.getTime() / 1000)
                    .issuedAt(issuedAt.getTime() / 1000)
                    .tokenType("Bearer")
                    .build());
        } catch (Exception e) {
            log.debug("Introspection failed for token: {}", e.getMessage());
            return ResponseEntity.ok(TokenIntrospectionResponse.builder().active(false).build());
        }
    }

    /**
     * Revokes an access token manually.
     */
    @PostMapping("/revoke")
    @Operation(summary = "Revoke access token", description = "Adds the token's JTI to the revocation blacklist")
    public ResponseEntity<String> revoke(@RequestParam("token") String token) {
        try {
            String jti = jwtService.extractClaim(token, Claims::getId);
            Date expiresAt = jwtService.extractClaim(token, Claims::getExpiration);

            if (jti != null) {
                revokedTokenRepository.save(RevokedToken.builder()
                        .jti(jti)
                        .expiresAt(expiresAt.toInstant())
                        .build());
            }

            return ResponseEntity.ok("Token revoked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to revoke token: " + e.getMessage());
        }
    }
}
