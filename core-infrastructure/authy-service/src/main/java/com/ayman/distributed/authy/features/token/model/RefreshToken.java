package com.ayman.distributed.authy.features.token.model;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a refresh token used for obtaining new access tokens
 * after the JWT expires, without forcing the user to log in again.
 *
 * Each refresh token belongs to a specific {@link User} and {@link Application}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_token_value", columnList = "token", unique = true),
                @Index(name = "idx_user_app", columnList = "user_id, app_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    /** Primary key for the refresh token. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* -------------------------------------------------------------------------
       RELATIONS
       ------------------------------------------------------------------------- */

    /** The user who owns this refresh token. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The application this refresh token was issued for. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    /* -------------------------------------------------------------------------
       TOKEN DATA
       ------------------------------------------------------------------------- */

    /** Secure random string identifying this refresh token. */
    @Column(nullable = false, unique = true, updatable = false, length = 255)
    private String token;

    /** Expiration timestamp (UTC). */
    @Column(nullable = false)
    private Instant expiresAt;

    /** Whether this token was revoked manually or replaced (token rotation). */
    @Column(nullable = false)
    private boolean revoked = false;

    /** Whether this token has already been used to obtain a new access token (for rotation). */
    @Column(nullable = false)
    private boolean used = false;

    /* -------------------------------------------------------------------------
       AUDITING
       ------------------------------------------------------------------------- */

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /* -------------------------------------------------------------------------
       CONSTRUCTORS & HELPERS
       ------------------------------------------------------------------------- */

    /**
     * Create a new refresh token for a given user and application.
     * @param user User who owns this token.
     * @param application Application for which the token is issued.
     * @param durationSeconds Expiration duration in seconds.
     */
    public RefreshToken(User user, Application application, long durationSeconds) {
        this.user = user;
        this.application = application;
        this.token = UUID.randomUUID().toString();
        this.expiresAt = Instant.now().plusSeconds(durationSeconds);
        this.revoked = false;
        this.used = false;
    }

    /** Marks the token as used (after access token refresh). */
    public void markUsed() {
        this.used = true;
    }

    /** Marks the token as revoked (manually invalidated). */
    public void revoke() {
        this.revoked = true;
    }

    /** Checks if the token is still valid. */
    public boolean isActive() {
        return !revoked && !used && Instant.now().isBefore(expiresAt);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "user=" + (user != null ? user.getUsername() : "null") +
                ", app=" + (application != null ? application.getAppKey() : "null") +
                ", expiresAt=" + expiresAt +
                ", active=" + isActive() +
                '}';
    }
}
