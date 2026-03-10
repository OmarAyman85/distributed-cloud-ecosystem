package com.ayman.distributed.simuclothing.controller;

import com.ayman.distributed.simuclothing.model.Order;
import com.ayman.distributed.simuclothing.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fashion/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.checkout(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getHistory(userDetails.getUsername()));
    }
}
