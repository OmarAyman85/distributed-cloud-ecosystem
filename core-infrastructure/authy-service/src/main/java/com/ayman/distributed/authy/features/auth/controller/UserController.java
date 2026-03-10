package com.ayman.distributed.authy.features.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.ayman.distributed.authy.features.identity.dto.UserResponseDTO;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user-related features, such as profile management.
 */
@Tag(name = "User", description = "Endpoints for user profile and settings")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves the profile details of the currently authenticated user.
     *
     * @param user The currently authenticated user from the Spring Security context.
     * @return User details as a DTO.
     */
    @Operation(summary = "Get current user profile", description = "Returns the profile information of the authenticated user")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        UserResponseDTO userDTO = userService.getUserDetails(user.getUsername());
        return ResponseEntity.ok(userDTO);
    }
}