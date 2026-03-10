package com.ayman.distributed.authy.features.token.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Entity to store revoked JWT IDs (JTIs).
 * Acts as a blacklist for access tokens.
 */
@Entity
@Table(name = "revoked_tokens", indexes = {
    @Index(name = "idx_jti", columnList = "jti", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique JWT ID (JTI) of the revoked token.
     */
    @Column(nullable = false, unique = true)
    private String jti;

    /**
     * When the token would have expired. 
     * Used for purging old entries from the database.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
