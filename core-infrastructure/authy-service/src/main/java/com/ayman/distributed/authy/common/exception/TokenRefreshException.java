package com.ayman.distributed.authy.common.exception;

import lombok.Getter;

@Getter
public class TokenRefreshException extends RuntimeException {
    private final String token;

    // Single parameter constructor (current one)
    public TokenRefreshException(String message) {
        super(message);
        this.token = null;
    }

    // Two parameter constructor (if you want to log the token)
    public TokenRefreshException(String token, String message) {
        super(message);
        this.token = token;
    }
}