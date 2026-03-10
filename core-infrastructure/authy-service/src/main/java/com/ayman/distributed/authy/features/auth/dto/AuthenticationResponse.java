package com.ayman.distributed.authy.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

/**
 * Encapsulates authentication results, including tokens and MFA status.
 */
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Response containing authentication tokens and state")
public class AuthenticationResponse {

    @JsonProperty("access_token")
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "Refresh token for obtaining new access tokens", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;

    @JsonProperty("mfa_enabled")
    @Schema(description = "Indicates if MFA is currently enabled for the user", example = "true")
    private boolean mfaEnabled;
    
    @JsonProperty("mfa_required")
    @Schema(description = "Indicates if the user must complete an MFA challenge to finish login", example = "false")
    private boolean mfaRequired;

    @JsonProperty("secret_image_uri")
    @Schema(description = "QR Code URI for MFA setup (returned during registration)", example = "otpauth://totp/...")
    private String secretImageUri;

    @JsonProperty("user_name")
    @Schema(description = "Username of the authenticated user", example = "johndoe")
    private String userName;
    
    @JsonProperty("pre_auth_token")
    @Schema(description = "Temporary token used for MFA verification after successful password validation", example = "eyJhbGciOiJIUzI1...")
    private String preAuthToken;

    // Constructor for Non-MFA Login Success
    public AuthenticationResponse(String accessToken, String refreshToken, String userName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.mfaEnabled = false;
        this.mfaRequired = false;
        this.secretImageUri = null;
        this.preAuthToken = null;
    }

    // Constructor for MFA Required (MFA is enabled, user needs to verify)
    public AuthenticationResponse(String preAuthToken, boolean mfaEnabled, String userName) {
        this.accessToken = null;
        this.refreshToken = null;
        this.userName = userName;
        this.mfaEnabled = mfaEnabled;
        this.mfaRequired = true; // Signals frontend to show OTP input
        this.secretImageUri = null;
        this.preAuthToken = preAuthToken;
    }

    // Constructor for Registration (returning QR code)
    public AuthenticationResponse(String accessToken, String refreshToken, boolean mfaEnabled, String secretImageUri, String userName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.mfaEnabled = mfaEnabled;
        this.mfaRequired = false;
        this.secretImageUri = secretImageUri;
        this.userName = userName;
        this.preAuthToken = null;
    }
}
