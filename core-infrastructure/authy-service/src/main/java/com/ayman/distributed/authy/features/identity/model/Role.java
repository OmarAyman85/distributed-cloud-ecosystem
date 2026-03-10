package com.ayman.distributed.authy.features.identity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Role within a specific {@link Application}.
 * Each application can have its own roles such as:
 * - CUSTOMER, SELLER, ADMIN for an e-commerce app.
 * - DRIVER, DEALER, INSPECTOR for a car marketplace app.
 *
 * Roles define the access level and permissions granted to users.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"app_id", "role_key"})
)
public class Role {

    /** Primary key for the role entity. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique key (per application) for this role — e.g., ADMIN, SELLER, CUSTOMER. */
    @Column(name = "role_key", nullable = false, length = 50)
    private String roleKey;

    /** Human-readable name for display (e.g., "Administrator", "Store Manager"). */
    @Column(nullable = false, length = 100)
    private String name;

    /** Optional text description explaining the role’s purpose or scope. */
    @Column(length = 255)
    private String description;

    /* -------------------------------------------------------------------------
       RELATIONS
       ------------------------------------------------------------------------- */

    /**
     * The {@link Application} this role belongs to.
     * A role cannot exist without being assigned to an application.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    /**
     * Collection of user-role mappings that assign this role to specific users.
     * This supports many-to-many mapping via the {@link UserRole} join table.
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

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
       CONVENIENCE METHODS
       ------------------------------------------------------------------------- */

    /**
     * Associates this role with an {@link Application}.
     * Keeps both sides of the relationship synchronized.
     */
    public void assignToApplication(Application app) {
        this.application = app;
        app.getRoles().add(this);
    }

    /**
     * Assigns this role to a user within a specific application.
     *
     * @param user The user being assigned.
     * @param app  The application context.
     * @return A new {@link UserRole} linking user ↔ app ↔ role.
     */
    public UserRole assignToUser(User user, Application app) {
        UserRole userRole = new UserRole(user, app, this);
        this.userRoles.add(userRole);
        user.getUserRoles().add(userRole);
        app.getUserRoles().add(userRole);
        return userRole;
    }

    /**
     * Removes a user-role association safely from all relationships.
     */
    public void removeUserRole(UserRole userRole) {
        this.userRoles.remove(userRole);
        userRole.setRole(null);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleKey='" + roleKey + '\'' +
                ", name='" + name + '\'' +
                ", app=" + (application != null ? application.getAppKey() : "null") +
                '}';
    }
}
