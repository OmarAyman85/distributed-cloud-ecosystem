package com.ayman.distributed.authy.features.identity.model;

import com.ayman.distributed.authy.features.identity.model.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an external application that integrates with the Authy authentication service.
 * Applications define the context for user roles and refresh tokens.
 */
@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Application {

    /** Primary key — unique ID for the application. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique short key for programmatic identification (e.g., SIMUCLOTHING, CARLOGER). */
    @Column(name = "app_key", unique = true, nullable = false, length = 50)
    private String appKey;

    /** Readable name of the application (e.g., "SimuClothing Fashion Store"). */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String appName;

    /** Optional text description of the application. */
    @Column(length = 255)
    private String description;

    /** Enum representing the application's lifecycle state (ACTIVE, INACTIVE, SUSPENDED, etc.). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    /* -------------------------------------------------------------------------
       Enhancements for OAuth / Multi-App Setup
       ------------------------------------------------------------------------- */

    /** Optional: URL or domain associated with this application (for redirection or callback). */
    @Column(name = "base_url", length = 255)
    private String baseUrl;

    /** Optional: Contact email for app owner or integration support. */
    @Column(name = "owner_email", length = 150)
    private String ownerEmail;

    /* -------------------------------------------------------------------------
       Auditing fields
       ------------------------------------------------------------------------- */

    /** Timestamp when the application was registered. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Timestamp of the last update to application details. */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /* -------------------------------------------------------------------------
       Relations
       ------------------------------------------------------------------------- */

    /** Roles available for this application (e.g., CUSTOMER, SELLER, ADMIN). */
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Role> roles = new HashSet<>();

    /** Users assigned to this application via their roles (many-to-many through UserRole). */
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    /* -------------------------------------------------------------------------
       Convenience Methods
       ------------------------------------------------------------------------- */

    /**
     * Adds a role to this application and synchronizes both sides of the relationship.
     */
    public void addRole(Role role) {
        roles.add(role);
        role.setApplication(this);
    }

    /**
     * Removes a role from this application and synchronizes both sides of the relationship.
     */
    public void removeRole(Role role) {
        roles.remove(role);
        role.setApplication(null);
    }
}
