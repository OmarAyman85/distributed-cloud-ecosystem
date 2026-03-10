package com.ayman.distributed.simuclothing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wardrobe_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardrobeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private WardrobeStatus status; // OWNED, WISHLIST

    private LocalDateTime addedAt;

    private String customNotes;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}


