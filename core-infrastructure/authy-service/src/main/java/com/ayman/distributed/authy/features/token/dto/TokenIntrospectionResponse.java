package com.ayman.distributed.authy.features.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Standard DTO for token introspection results.
 * Based on RFC 7662 (OAuth 2.0 Token Introspection).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenIntrospectionResponse {

    /**
     * Boolean indicator of whether or not the presented token is currently active.
     */
    private boolean active;

    /**
     * Subject of the token (username).
     */
    @JsonProperty("sub")
    private String subject;

    /**
     * Authentication methods references (e.g. mfa).
     */
    private String amr;

    /**
     * Scopes or permissions associated with the token.
     */
    private String scope;

    /**
     * Client identifier (App Key).
     */
    @JsonProperty("client_id")
    private String appKey;

    /**
     * Issued-at timestamp (seconds since epoch).
     */
    @JsonProperty("iat")
    private Long issuedAt;

    /**
     * Expiration timestamp (seconds since epoch).
     */
    @JsonProperty("exp")
    private Long expiresAt;

    /**
     * Roles associated with the user/app context.
     */
    private List<String> roles;

    /**
     * Type of token (usually Bearer).
     */
    @JsonProperty("token_type")
    private String tokenType;
}
