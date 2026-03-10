package com.ayman.distributed.authy.features.identity.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Represents an external authentication provider account linked to a User.
 * Supports account linking for Social Login (OAuth2, OpenID Connect).
 */
@Entity
@Table(name = "user_providers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider_name", "provider_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the provider (e.g., GOOGLE, GITHUB).
     */
    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName;

    /**
     * External unique identifier from the provider.
     */
    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    /**
     * The system user this provider account is linked to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
