package com.ayman.distributed.authy.features.identity.model;
import com.ayman.distributed.authy.features.token.model.RefreshToken;

import java.time.LocalDateTime;
import com.ayman.distributed.authy.features.identity.model.Address;
import com.ayman.distributed.authy.features.identity.model.Gender;
import com.ayman.distributed.authy.features.identity.model.UserStatus;
import com.ayman.distributed.authy.features.mfa.model.MfaMethod;
import com.ayman.distributed.authy.features.mfa.model.MfaRecoveryCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

/**
 * Represents a system user authenticated through the Authy service.
 * This entity supports integration with multiple applications, roles,
 * and refresh tokens, and implements {@link UserDetails} for Spring Security.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    /**
     * Primary key, generated UUID for global uniqueness across services.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* --------------------------- Authentication Fields --------------------------- */

    /**
     * User email, unique across the system.
     */
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    /**
     * Unique username, used for login.
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Securely hashed password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Represents the user's current account state.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /* --------------------------- Personal Information --------------------------- */

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 10)
    private String locale;

    @Column(length = 50)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "birth_date")
    private Instant birthDate;

    /* --------------------------- Contact & Address --------------------------- */

    @Column(name = "mobile_phone", unique = true, length = 20)
    private String mobilePhone;

    @Embedded
    private Address address;

    /* --------------------------- MFA & Auth Provider --------------------------- */

    @Column(nullable = false)
    private boolean mfaEnabled;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(nullable = false)
    private boolean isEmailVerified;

    @Column(name = "auth_provider")
    private String authProvider;

    @Column(name = "auth_provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_mfa_method")
    private MfaMethod preferredMfaMethod = MfaMethod.TOTP;

    /* --------------------------- Relations --------------------------- */

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProvider> providers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MfaRecoveryCode> recoveryCodes = new HashSet<>();

    /* --------------------------- Auditing --------------------------- */

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /* --------------------------- Security Implementation --------------------------- */
    /**
     * Maps the user's roles for the specific applications into Spring Security authorities.
     * @return Collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        // Map UserRole to SimpleGrantedAuthority using the role's key (e.g., ROLE_ADMIN)
        return userRoles.stream().map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleKey())).toList();
    }

    /**
     * @return true as accounts are not set to expire by default.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Checks if the account is not locked.
     * @return true if status is not LOCKED.
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.status != UserStatus.LOCKED;
    }

    /**
     * @return true as credentials are not set to expire by default.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if the user is enabled for login.
     * Requires the account to be ACTIVE and the email to be verified.
     * @return true if enabled.
     */
    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
    }
}