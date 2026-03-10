package com.ayman.distributed.simuclothing.controller;

import com.ayman.distributed.simuclothing.model.WardrobeItem;
import com.ayman.distributed.simuclothing.model.WardrobeStatus;
import com.ayman.distributed.simuclothing.service.WardrobeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fashion/wardrobe")
@RequiredArgsConstructor
public class WardrobeController {

    private final WardrobeService wardrobeService;

    @GetMapping
    public ResponseEntity<List<WardrobeItem>> getWardrobe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) WardrobeStatus status
    ) {
        return ResponseEntity.ok(wardrobeService.getWardrobe(userDetails.getUsername(), status));
    }

    @PostMapping("/add")
    public ResponseEntity<WardrobeItem> addToWardrobe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam WardrobeStatus status
    ) {
        return ResponseEntity.ok(wardrobeService.addToWardrobe(userDetails.getUsername(), productId, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromWardrobe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        wardrobeService.removeFromWardrobe(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}
