package com.ayman.distributed.authy.features.identity.model;

/**
 * Represents a user's gender identity.
 * Used for profile information and demographic data across all connected applications.
 */
public enum Gender {

    /** Male gender. */
    MALE("Male"),

    /** Female gender. */
    FEMALE("Female"),

    /** Non-binary, genderqueer, or otherwise outside the male/female binary. */
    OTHER("Other"),

    /** User chooses not to disclose their gender. */
    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a user-friendly label for display in UI or API responses.
     */
    public String getDisplayName() {
        return displayName;
    }
}
