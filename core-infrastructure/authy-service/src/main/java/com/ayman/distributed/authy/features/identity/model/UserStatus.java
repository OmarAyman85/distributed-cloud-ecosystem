package com.ayman.distributed.authy.features.identity.model;

/**
 * Represents the current lifecycle or restriction state of a user account.
 * Used by the Authy service to control authentication and access.
 */
public enum UserStatus {

    /** The user is fully active and allowed to authenticate. */
    ACTIVE,

    /** The user registered but hasn’t verified email or completed profile. */
    PENDING_VERIFICATION,

    /** The user is temporarily blocked (e.g., too many failed logins). */
    BLOCKED,

    /** The user violated policy and is permanently banned. */
    BANNED,

    /** The user requested account deactivation (soft delete). */
    DEACTIVATED,

    LOCKED,

    /** The account was removed permanently (hard delete or admin action). */
    DELETED;

    /**
     * Check if user is in a state that allows login.
     */
    public boolean canAuthenticate() {
        return this == ACTIVE;
    }

    /**
     * Check if user is temporarily restricted.
     */
    public boolean isTemporarilyRestricted() {
        return this == BLOCKED;
    }

    /**
     * Check if user is permanently disabled.
     */
    public boolean isPermanentlyBanned() {
        return this == BANNED || this == DELETED;
    }
}
