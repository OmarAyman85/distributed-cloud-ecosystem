package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.User;

import com.ayman.distributed.authy.features.identity.model.UserRole;
import com.ayman.distributed.authy.features.identity.model.Gender;
import com.ayman.distributed.authy.features.identity.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing a user's profile information.
 */
@Schema(description = "User profile information")
public record UserResponseDTO(
        @Schema(description = "Unique identifier of the user", example = "1")
        Long id,

        @Schema(description = "User's email address", example = "john@example.com")
        String email,

        @Schema(description = "User's unique username", example = "johndoe")
        String username,

        @Schema(description = "User's first name", example = "John")
        String firstName,

        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @Schema(description = "User's full name", example = "John Doe")
        String fullName,

        @Schema(description = "User's gender")
        Gender gender,

        @Schema(description = "User's date of birth")
        Instant birthDate,

        @Schema(description = "User's mobile phone number", example = "+1234567890")
        String mobilePhone,

        @Schema(description = "User's primary address")
        AddressDTO address,

        @Schema(description = "Current account status")
        UserStatus status,

        @Schema(description = "Whether the user's email has been verified", example = "true")
        boolean isEmailVerified,

        @Schema(description = "Whether multi-factor authentication is enabled", example = "true")
        boolean mfaEnabled,

        @Schema(description = "Timestamp when the user account was created")
        Instant createdAt,

        @Schema(description = "Roles assigned to the user across different applications")
        Set<UserRole> userRoles,

        @Schema(description = "The external authentication provider (if any)", example = "google")
        String authProvider,

        @Schema(description = "The user ID assigned by the external provider", example = "123456789")
        String providerId
) {
    public String fullName() {
        return firstName + " " + lastName;
    }
}
