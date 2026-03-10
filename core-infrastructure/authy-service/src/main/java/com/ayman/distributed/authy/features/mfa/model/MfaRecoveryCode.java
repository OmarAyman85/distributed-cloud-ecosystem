package com.ayman.distributed.authy.features.mfa.model;

import com.ayman.distributed.authy.features.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Entity to store hashed MFA recovery codes.
 */
@Entity
@Table(name = "mfa_recovery_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MfaRecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Hashed recovery code.
     */
    @Column(nullable = false)
    private String hashedCode;

    /**
     * Whether this specific code has been used.
     */
    @Column(nullable = false)
    private boolean used;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "used_at")
    private Instant usedAt;
}
