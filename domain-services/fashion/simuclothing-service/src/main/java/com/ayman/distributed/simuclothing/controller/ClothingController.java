package com.ayman.distributed.simuclothing.controller;

import com.ayman.distributed.simuclothing.model.ClothingItem;
import com.ayman.distributed.simuclothing.service.ClothingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clothing")
@RequiredArgsConstructor
public class ClothingController {

    private final ClothingService service;

    @PostMapping
    public ResponseEntity<ClothingItem> addItem(@Valid @RequestBody ClothingItem item) {
        return ResponseEntity.ok(service.addItem(item));
    }

    @GetMapping
    public ResponseEntity<List<ClothingItem>> getAllItems() {
        return ResponseEntity.ok(service.getAllItems());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ClothingItem>> getItemsByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getItemsByType(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClothingItem> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItemById(id));
    }
}
