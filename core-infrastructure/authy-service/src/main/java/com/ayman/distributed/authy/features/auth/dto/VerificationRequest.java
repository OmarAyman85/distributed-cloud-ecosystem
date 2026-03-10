package com.ayman.distributed.authy.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Data Transfer Object for multi-factor authentication code verification.
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "MFA verification request")
public class VerificationRequest {

    @Schema(description = "Unique key of the application context", example = "KITCHEN_STORE")
    private final String appKey;

    @Schema(description = "Username of the user attempting to verify", example = "johndoe")
    private final String username;

    @Schema(description = "The 6-digit TOTP code from the user's authenticator app", example = "123456")
    private final String code;

    @Schema(description = "Temporary pre-auth token received during login", example = "eyJhbGciOiJIUzI1...")
    private final String preAuthToken;
}
