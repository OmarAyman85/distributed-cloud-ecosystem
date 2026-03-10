package com.ayman.distributed.simuclothing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Version
    private Long version; // Optimistic locking

    @Enumerated(EnumType.STRING)
    private Category category;

    private String brand;
    private String size; // Could be Enum, but String for flexibility (S, M, L, XL, 32, 34)
    private String color;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
}

enum Category {
    SHIRT, PANTS, SHOES, ACCESSORY, OUTERWEAR, DRESS, SKIRT, ACTIVEWEAR
}
