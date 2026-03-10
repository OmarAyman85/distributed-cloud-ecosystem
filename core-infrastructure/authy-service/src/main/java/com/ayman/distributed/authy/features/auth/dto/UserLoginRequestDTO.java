package com.ayman.distributed.authy.features.auth.dto;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.identity.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login.
 * Supports email, username, or phone number as an identifier.
 */
@Schema(description = "User login request")
public record UserLoginRequestDTO(
        @NotBlank 
        @Schema(description = "Unique key of the application context", example = "KITCHEN_STORE")
        String appKey,

        @NotBlank 
        @Schema(description = "User identifier (email, username, or mobile phone)", example = "john@example.com")
        String identifier,

        @NotBlank 
        @Schema(description = "User password", example = "Password123!")
        String password
) {
}

