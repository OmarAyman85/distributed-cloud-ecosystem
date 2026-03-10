package com.ayman.distributed.authy.features.identity.dto;

import com.ayman.distributed.authy.features.identity.model.Gender;
import jakarta.validation.constraints.NotBlank;

/**
 * Registration for users signing up with a mobile phone number only.
 */
public record PhoneUserRegistrationRequestDTO(
        @NotBlank String mobilePhone,
        @NotBlank String username,
        String firstName,
        String lastName,
        Gender gender
) {
}

//if (identifier.contains("@")) {
//        // treat as email
//        } else if (identifier.matches("\\d+")) {
//        // treat as phone number
//        } else {
//        // treat as username
//        }

