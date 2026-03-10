package com.ayman.distributed.simuclothing.controller;

import com.ayman.distributed.simuclothing.model.Cart;
import com.ayman.distributed.simuclothing.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fashion/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(cartService.addItem(userDetails.getUsername(), productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUsername(), productId));
    }
}
