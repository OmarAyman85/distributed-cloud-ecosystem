package com.ayman.distributed.authy.features.token.model;

/**
 * Enumerates the types of tokens managed by the authentication system.
 * Tokens are used for authentication, authorization, and session continuity.
 */
public enum TokenType {

    /**
     * A short-lived access token (typically JWT) used for authorized API requests.
     * Commonly included in the Authorization header as: "Bearer <token>".
     */
    BEARER,

    /**
     * A long-lived token used to obtain new access tokens without re-authenticating.
     * Typically stored securely (e.g., HTTP-only cookie).
     */
    REFRESH,

    /**
     * Optional type for API key-based authentication (e.g., service-to-service calls).
     * Could be added later if external integrations need static tokens.
     */
    API_KEY,

    /**
     * Optional type for one-time tokens used in password resets or email verification.
     */
    ONE_TIME;

    /**
     * Indicates whether this token type can be used directly for resource access.
     * (e.g., BEARER tokens are used in Authorization headers, REFRESH tokens are not)
     */
    public boolean isAccessToken() {
        return this == BEARER;
    }

    /**
     * Indicates whether this token is long-lived and used for session renewal.
     */
    public boolean isRefreshToken() {
        return this == REFRESH;
    }

    /**
     * Indicates if the token is meant for limited-scope or one-time actions.
     */
    public boolean isTemporary() {
        return this == ONE_TIME;
    }
}
