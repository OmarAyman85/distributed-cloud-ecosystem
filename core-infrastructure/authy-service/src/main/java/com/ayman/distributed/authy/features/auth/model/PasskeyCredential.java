package com.ayman.distributed.authy.features.auth.model;

import com.ayman.distributed.authy.features.identity.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Entity to store Passkey (WebAuthn/FIDO2) credentials for a user.
 */
@Entity
@Table(name = "passkey_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PasskeyCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "credential_id", nullable = false, unique = true)
    private String credentialId;

    @Lob
    @Column(name = "public_key", nullable = false)
    private byte[] publicKey;

    @Column(name = "sign_count", nullable = false)
    private long signCount;

    @Column(name = "transports")
    private String transports; // Comma-separated list e.g., "usb,nfc,ble,internal"

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;
}
