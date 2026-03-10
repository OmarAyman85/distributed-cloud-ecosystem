package com.ayman.distributed.authy.features.auth.controller;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.identity.model.User;

import com.ayman.distributed.authy.features.auth.dto.AuthenticationResponse;
import com.ayman.distributed.authy.features.auth.dto.VerificationRequest;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;

import com.ayman.distributed.authy.features.auth.service.AuthenticationService;
import com.ayman.distributed.authy.features.auth.service.MagicLinkService;
import com.ayman.distributed.authy.features.identity.service.ProfilePictureService;
import com.ayman.distributed.authy.features.mfa.model.MfaMethod;
import com.ayman.distributed.authy.features.mfa.service.MfaService;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller responsible for handling authentication-related requests such as user registration, login,
 * verification, and token management.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and MFA management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ProfilePictureService profilePictureService;
    private final MagicLinkService magicLinkService;
    private final MfaService mfaService;
    private final UserRepository userRepository;

    /**
     * Default endpoint providing a welcome message.
     * @return A welcome string.
     */
    @GetMapping("")
    @Operation(summary = "Home endpoint", description = "Returns a simple welcome message")
    public String home() {
        return "Welcome to Authy";
    }

    /**
     * Registers a new user with standard credentials.
     * @param request Registration details including username, email, password, and MFA preferences.
     * @return Authentication response with tokens if MFA is disabled, or MFA setup details if enabled.
     */
    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user with standard credentials and optional MFA setup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody StandardUserRegistrationRequestDTO request) {
        try {
            AuthenticationResponse response = authenticationService.register(request);

            // If MFA is enabled, return the QR code and setup info
            // If MFA is disabled, return tokens (but with 202 Accepted for email verification flow)
            if (request.mfaEnabled()) {
                return ResponseEntity.ok(response);
            } else {
                // For non-MFA users, you might want to return the tokens
                // but indicate that email verification is pending
                return ResponseEntity.ok(response); // Changed from accepted() to ok()
            }
        } catch (IllegalArgumentException ex) {
            // Handle specific validation errors (username/email exists)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + ex.getMessage());
        }
    }

    /**
     * Authenticates a user and issues access and refresh tokens.
     * @param request Login credentials (username/email/phone and password).
     * @param response HTTP response to set the refresh token cookie.
     * @return Authentication response with access token and user details.
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user using multi-identifier support and returns access/refresh tokens.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthenticationResponse> loginUser(
            @Valid @RequestBody UserLoginRequestDTO request,
            HttpServletResponse response
    ) {
        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        // Set refresh token as HTTP-only cookie if provided
        if (authResponse.getRefreshToken() != null) {
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // TODO: Set to true in production with HTTPS
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Verifies a user's MFA authentication code.
     * @param verificationRequest Verification details including username and TOTP code.
     * @return Response indicating verification success with tokens, or failure.
     */
    @PostMapping("/verify-code")
    @Operation(summary = "Verify MFA code", description = "Verifies the TOTP code for users with MFA enabled")
    public ResponseEntity<AuthenticationResponse> verifyCode(
            @Valid @RequestBody VerificationRequest verificationRequest,
            HttpServletResponse response
    ) {
        AuthenticationResponse authResponse = authenticationService.verifyCode(verificationRequest);

        // Set refresh token cookie after successful MFA verification
        if (authResponse.getRefreshToken() != null) {
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // TODO: Set to true in production
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Uploads a profile picture for the authenticated user.
     * @param file The profile picture file.
     * @return The URL of the uploaded profile picture.
     */
    @PostMapping("/upload-profile-picture")
    @Operation(summary = "Upload profile picture", description = "Uploads a profile picture for the authenticated user")
    public ResponseEntity<String> uploadProfilePicture(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String fileUrl = profilePictureService.storeProfilePicture(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    /**
     * Refreshes an authentication token using the refresh token from the cookie.
     * @param request HTTP request containing the refresh token cookie.
     * @param response HTTP response to set the new refresh token cookie.
     * @return Response containing new access and refresh tokens.
     * @throws IOException If an error occurs while processing the request.
     */
    @GetMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Issues a new access token using the refresh token stored in a secure cookie")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        return authenticationService.refreshToken(request, response);
    }

    /**
     * Requests a magic link for passwordless login.
     * @param email The user's email address.
     * @param appKey The application key.
     * @return 200 OK.
     */
    @PostMapping("/magic-link/request")
    @Operation(summary = "Request magic link", description = "Generates and sends a magic link to the user's email")
    public ResponseEntity<String> requestMagicLink(
            @RequestParam String email,
            @RequestParam(defaultValue = "AUTHY") String appKey
    ) {
        try {
            magicLinkService.sendMagicLink(email, appKey);
            return ResponseEntity.ok("Magic link sent successfully. Please check your email (and logs).");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Verifies a magic link token and performs login.
     * @param token The magic link token.
     * @param appKey The application key.
     * @param response HTTP response to set the refresh token cookie.
     * @return Authentication response with tokens.
     */
    @GetMapping("/magic-link/verify")
    @Operation(summary = "Verify magic link", description = "Verifies the magic link token and authenticates the user")
    public ResponseEntity<AuthenticationResponse> verifyMagicLink(
            @RequestParam String token,
            @RequestParam(defaultValue = "AUTHY") String appKey,
            HttpServletResponse response
    ) {
        AuthenticationResponse authResponse = magicLinkService.verifyMagicLink(token, appKey);

        // Set refresh token cookie
        if (authResponse.getRefreshToken() != null) {
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // TODO: true in prod
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Logs out the user and invalidates tokens.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Revokes access/refresh tokens and clears the refresh token cookie")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request);
        
        // Clear refresh token cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // TODO: true in prod
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }
}