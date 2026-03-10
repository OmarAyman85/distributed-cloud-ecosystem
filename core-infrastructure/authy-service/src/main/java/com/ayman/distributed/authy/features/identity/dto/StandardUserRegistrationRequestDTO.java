package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.User;

import com.ayman.distributed.authy.features.identity.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Data Transfer Object for standard user registration using email, username, and password.
 */
@Schema(description = "Standard user registration request")
public record StandardUserRegistrationRequestDTO(
        @NotBlank
        @Schema(description = "Unique key of the application the user is registering for", example = "KITCHEN_STORE")
        String appKey,

        @NotBlank @Email 
        @Schema(description = "User's unique email address", example = "user@example.com")
        String email,

        @NotBlank @Size(min = 4, max = 50) 
        @Schema(description = "Unique username for the user", example = "johndoe")
        String username,

        @NotBlank @Size(min = 8) 
        @Schema(description = "Secure password for the user", example = "Password123!")
        String password,

        @NotBlank 
        @Schema(description = "User's first name", example = "John")
        String firstName,

        @NotBlank 
        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @Schema(description = "User's display name", example = "John D.")
        String displayName,

        @Schema(description = "URL to the user's avatar image")
        String avatarUrl,

        @Schema(description = "User's preferred locale (e.g., en-US, ar-EG)", example = "en-US")
        String locale,

        @Schema(description = "User's timezone (e.g., Africa/Cairo)", example = "Africa/Cairo")
        String timezone,

        @Schema(description = "User's date of birth")
        Instant birthDate,

        @Schema(description = "User's gender")
        Gender gender,

        @Schema(description = "User's mobile phone number", example = "+1234567890")
        String mobilePhone,

        @Schema(description = "Whether to enable Multi-Factor Authentication for this user", example = "true")
        boolean mfaEnabled
) {
}

