package com.ayman.distributed.simuclothing.controller;

import com.ayman.distributed.simuclothing.model.Outfit;
import com.ayman.distributed.simuclothing.service.OutfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fashion/outfits")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitService outfitService;

    @GetMapping
    public ResponseEntity<List<Outfit>> getOutfits(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(outfitService.getOutfits(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<Outfit> createOutfit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OutfitRequest request
    ) {
        return ResponseEntity.ok(outfitService.createOutfit(
                userDetails.getUsername(),
                request.outfitData(),
                request.wardrobeItemIds()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutfit(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        outfitService.deleteOutfit(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}

record OutfitRequest(Outfit outfitData, List<Long> wardrobeItemIds) {}
