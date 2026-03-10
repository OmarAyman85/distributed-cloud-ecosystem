package com.ayman.distributed.authy.features.mfa.model;

/**
 * Supported Multi-Factor Authentication methods.
 */
public enum MfaMethod {
    /**
     * Time-based One-Time Password (App-based, e.g., Google Authenticator).
     */
    TOTP,

    /**
     * Email-based One-Time Password.
     */
    EMAIL,

    /**
     * SMS-based One-Time Password.
     */
    SMS
}
