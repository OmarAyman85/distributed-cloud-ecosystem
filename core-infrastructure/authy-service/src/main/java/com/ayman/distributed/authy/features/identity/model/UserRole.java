package com.ayman.distributed.authy.features.identity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Represents the relationship between a {@link User}, a {@link Role}, and an {@link Application}.
 *
 * This entity enables fine-grained user access control across multiple applications.
 * A user may have different roles in different apps (e.g. CUSTOMER in SimuClothing, DEALER in CarLoger).
 *
 * Composite uniqueness constraint ensures a user cannot have the same role twice within the same app.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "user_roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "app_id", "role_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class UserRole {

    /** Primary key for the user-role relation. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* -------------------------------------------------------------------------
       RELATIONS
       ------------------------------------------------------------------------- */

    /**
     * The user who owns this role assignment.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The application in which this user-role assignment applies.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    private Application application;

    /**
     * The role assigned to the user within this application.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /* -------------------------------------------------------------------------
       AUDITING
       ------------------------------------------------------------------------- */

    /** Timestamp of when the relation was created. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Timestamp of the last modification. */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /* -------------------------------------------------------------------------
       CONSTRUCTORS
       ------------------------------------------------------------------------- */

    /**
     * Convenient constructor for building a user-role mapping directly.
     *
     * @param user        The user to assign.
     * @param application The application context.
     * @param role        The role assigned to the user.
     */
    public UserRole(User user, Application application, Role role) {
        this.user = user;
        this.application = application;
        this.role = role;
    }

    /* -------------------------------------------------------------------------
       HELPER METHODS
       ------------------------------------------------------------------------- */

    /**
     * Synchronizes both sides of the relationship (User, Role, Application).
     * This helps maintain JPA consistency when assigning new relations.
     */
    public void linkBidirectionally() {
        if (user != null) user.getUserRoles().add(this);
        if (application != null) application.getUserRoles().add(this);
        if (role != null) role.getUserRoles().add(this);
    }

    /**
     * Safely removes this relation from all associated entities.
     * Prevents memory leaks and stale references when deleting roles or users.
     */
    public void unlinkBidirectionally() {
        if (user != null) user.getUserRoles().remove(this);
        if (application != null) application.getUserRoles().remove(this);
        if (role != null) role.getUserRoles().remove(this);
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "user=" + (user != null ? user.getUsername() : "null") +
                ", role=" + (role != null ? role.getRoleKey() : "null") +
                ", app=" + (application != null ? application.getAppKey() : "null") +
                '}';
    }
}
