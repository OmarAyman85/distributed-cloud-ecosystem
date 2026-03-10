package com.ayman.distributed.authy.features.audit.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String actor; // Details of who performed the action (username or system)

    private String action; // The method or operation name

    private String resource; // The target resource (e.g., User ID, Order ID)

    private String result; // SUCCESS or FAILURE

    private String ipAddress;

    @Column(length = 2000)
    private String details; // JSON or text details
}
