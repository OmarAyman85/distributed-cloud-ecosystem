package com.ayman.distributed.simuclothing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;


    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    
    // Store price at time of adding to cart? Or dynamic?
    // Dynamic usually, but for CartItem display usage it's fine to fetch from product.
    // However, let's store a snapshot or just use product reference.
    
    public BigDecimal getPrice() {
        return product.getPrice();
    }
}
