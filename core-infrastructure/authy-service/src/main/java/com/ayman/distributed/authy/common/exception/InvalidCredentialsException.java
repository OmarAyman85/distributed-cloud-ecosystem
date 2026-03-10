package com.ayman.distributed.authy.common.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String s) {
        super("Invalid credentials");
    }
}
