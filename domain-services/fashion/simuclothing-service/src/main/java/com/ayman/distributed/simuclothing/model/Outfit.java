package com.ayman.distributed.simuclothing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "outfits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Occasion occasion;

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "outfit_items",
        joinColumns = @JoinColumn(name = "outfit_id"),
        inverseJoinColumns = @JoinColumn(name = "wardrobe_item_id")
    )
    private Set<WardrobeItem> items = new HashSet<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
