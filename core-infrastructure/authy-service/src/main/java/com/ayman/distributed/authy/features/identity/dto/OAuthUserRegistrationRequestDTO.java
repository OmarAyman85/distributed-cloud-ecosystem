package com.ayman.distributed.authy.features.identity.dto;

import com.ayman.distributed.authy.features.identity.model.Gender;
import jakarta.validation.constraints.NotBlank;

/**
 * Registration request for users signing up via an OAuth2 provider.
 */
public record OAuthUserRegistrationRequestDTO(
        @NotBlank String authProvider,    // e.g. GOOGLE, GITHUB
        @NotBlank String providerId, // The unique ID from provider
        String email,
        String username,
        String firstName,
        String lastName,
        Gender gender,
        String mobilePhone
) {
}

//  provider maps to your authProvider field in the entity.