package com.ayman.distributed.simuclothing.controller;

import com.ayman.distributed.simuclothing.model.FashionProfile;
import com.ayman.distributed.simuclothing.service.FashionProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fashion/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final FashionProfileService profileService;

    @GetMapping
    public ResponseEntity<FashionProfile> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(profileService.getProfile(userDetails.getUsername()));
    }

    @PutMapping
    public ResponseEntity<FashionProfile> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FashionProfile profile
    ) {
        return ResponseEntity.ok(profileService.updateProfile(userDetails.getUsername(), profile));
    }
}
