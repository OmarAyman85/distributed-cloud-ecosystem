package com.ayman.distributed.authy.features.token.service;

import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.token.repository.RevokedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

/**
 * Service responsible for handling JWT operations such as token generation, validation, and claim extraction.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    @Value("${REFRESH_EXPIRATION}")
    private long refreshExpiration;

    private final RevokedTokenRepository revokedTokenRepository;
    
    // 5 minutes for Pre-Auth token
    private static final long PRE_AUTH_EXPIRATION = 300000; 
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";
    private static final String TYPE_PRE_AUTH = "PRE_AUTH";

    /**
     * Extracts the username (subject) from the given JWT token.
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates an access token.
     */
    public boolean isValidToken(String token, UserDetails user) {
        String username = getUsernameFromToken(token);
        String type = extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
        return username.equals(user.getUsername()) && !isTokenExpired(token) && TYPE_ACCESS.equals(type) && !isTokenRevoked(token);
    }

    /**
     * Checks if a token (specifically its JTI) has been revoked.
     */
    public boolean isTokenRevoked(String token) {
        String jti = extractClaim(token, Claims::getId);
        if (jti == null) return false;
        return revokedTokenRepository.findByJti(jti).isPresent();
    }

    /**
     * Revokes a token by its value.
     */
    public void revokeToken(String token) {
        String jti = extractClaim(token, Claims::getId);
        Date expiresAt = extractClaim(token, Claims::getExpiration);
        if (jti != null && expiresAt != null) {
            revokedTokenRepository.save(com.ayman.distributed.authy.features.token.model.RevokedToken.builder()
                    .jti(jti)
                    .expiresAt(expiresAt.toInstant())
                    .build());
        }
    }

    /**
     * Validates the given refresh token.
     */
    public boolean isValidRefreshToken(String token, User user) {
        String username = getUsernameFromToken(token);
        String type = extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
        return username.equals(user.getUsername()) && !isTokenExpired(token) && TYPE_REFRESH.equals(type);
    }
    
    /**
     * Validates if the token is a valid PRE_AUTH token.
     */
    public boolean isValidPreAuthToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        String type = extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
        return tokenUsername.equals(username) && !isTokenExpired(token) && TYPE_PRE_AUTH.equals(type);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserDetails user) {
        return buildToken(user, jwtExpiration, TYPE_ACCESS, null, Collections.emptyList());
    }

    public String generateToken(UserDetails user, String appKey, List<String> roles) {
        return buildToken(user, jwtExpiration, TYPE_ACCESS, appKey, roles);
    }

    public String generateRefreshToken(UserDetails user) {
        return buildToken(user, refreshExpiration, TYPE_REFRESH, null, Collections.emptyList());
    }

    public String generateRefreshToken(UserDetails user, String appKey, List<String> roles) {
        return buildToken(user, refreshExpiration, TYPE_REFRESH, appKey, roles);
    }
    
    public String generatePreAuthToken(String username) {
         return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim(CLAIM_TYPE, TYPE_PRE_AUTH)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + PRE_AUTH_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    private String buildToken(UserDetails user, long expiration, String type,
                               String appKey, List<String> roles) {
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .claim(CLAIM_TYPE, type)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration));

        if (appKey != null) {
            builder.claim("app_key", appKey);
        }
        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        return builder.signWith(getSigningKey()).compact();
    }

    public String extractAppKey(String token) {
        return extractClaim(token, claims -> claims.get("app_key", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            List<String> r = claims.get("roles", List.class);
            return r != null ? r : Collections.emptyList();
        });
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
