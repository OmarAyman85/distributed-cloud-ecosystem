package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.User;

import com.ayman.distributed.authy.features.identity.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Data Transfer Object for updating user profile information.
 */
@Schema(description = "User profile update request")
public record UserUpdateRequestDTO(
        @Size(max = 50) 
        @Schema(description = "Updated first name", example = "John")
        String firstName,

        @Size(max = 50) 
        @Schema(description = "Updated last name", example = "Doe")
        String lastName,

        @Schema(description = "Updated mobile phone number", example = "+1234567890")
        String mobilePhone,

        @Schema(description = "Updated address details")
        AddressDTO address,

        @Schema(description = "Updated date of birth")
        Instant birthDate,

        @Schema(description = "Updated gender")
        Gender gender
) {}
