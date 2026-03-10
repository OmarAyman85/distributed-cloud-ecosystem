package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.*;
import com.ayman.distributed.simuclothing.repository.CartRepository;
import com.ayman.distributed.simuclothing.repository.OrderRepository;
import com.ayman.distributed.simuclothing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final WardrobeService wardrobeService;

    @Transactional
    public Order checkout(String userId) {
        Cart cart = cartService.getCart(userId);
        
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 1. Create Order
        Order order = Order.builder()
                .userId(userId)
                .totalAmount(cart.getTotalAmount())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            // 2. Inventory Check & Update
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            
            // 3. Add to Wardrobe (OWNED)
            wardrobeService.addToWardrobe(userId, product.getId(), WardrobeStatus.OWNED);

            return OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        
        // 4. Clear Cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderRepository.save(order);
    }
    
    public List<Order> getHistory(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
