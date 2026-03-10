package com.ayman.distributed.authy.features.identity.model;

/**
 * Defines the operational state of an application registered in the Authy service.
 * This helps control login and token issuance per app.
 */
public enum ApplicationStatus {

    /** The application is active and can authenticate users. */
    ACTIVE,

    /** The application is temporarily disabled (e.g., maintenance mode). */
    INACTIVE,

    /** The application is pending admin approval or review. */
    PENDING_APPROVAL,

    /** The application was permanently deactivated or removed. */
    DECOMMISSIONED;

    /**
     * Utility method to check if the app is currently usable.
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Returns true if this app is in any non-operational state.
     */
    public boolean isDisabled() {
        return this != ACTIVE;
    }
}
