package com.ayman.distributed.simuclothing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "clothing_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClothingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String type; // e.g., Shirt, Pants, Dress

    @NotBlank
    private String size; // S, M, L, XL

    @NotBlank
    private String color;

    @NotNull
    @Positive
    private BigDecimal price;

    private String imageUrl;
}
