package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.Cart;
import com.ayman.distributed.simuclothing.model.CartItem;
import com.ayman.distributed.simuclothing.model.Product;
import com.ayman.distributed.simuclothing.repository.CartRepository;
import com.ayman.distributed.simuclothing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = Cart.builder().userId(userId).build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public Cart addItem(String userId, Long productId, int quantity) {
        Cart cart = getCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .product(product)

                    .quantity(quantity)
                    .build();
            cart.addItem(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(String userId, Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }
}
