package com.ayman.distributed.authy.features.identity.repository;

import com.ayman.distributed.authy.features.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link User} entities.
 * Extends JpaRepository for standard CRUD operations and provides
 * custom methods essential for the authentication and registration processes.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // --- Primary Retrieval Methods (Used by UserDetailsService & Login) ---

    /**
     * Finds a user by their unique email address.
     * This is typically the primary method used by the Spring Security UserDetailsService.
     *
     * @param email The unique email address of the user.
     * @return An {@link Optional} containing the user if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their unique username.
     *
     * @param username The unique username of the user.
     * @return An {@link Optional} containing the user if found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by either their username OR email OR mobile phone number.
     * Essential for "Multi-Identifier" login support.
     *
     * @param identifier The identifier (username, email, or phone) provided by the user.
     * @return An {@link Optional} containing the user if found.
     */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier OR u.mobilePhone = :identifier")
    Optional<User> findByIdentifier(String identifier);

    /**
     * Finds a user by either their username OR email. Useful for a flexible login endpoint.
     * NOTE: Database query efficiency may vary compared to the single-field finders above.
     *
     * @param username The username to search for.
     * @param email    The email to search for.
     * @return An {@link Optional} containing the user if found.
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    // --- Uniqueness Check Methods (Used During Registration) ---

    /**
     * Checks if a user already exists with the given email address.
     *
     * @param email The email address to check.
     * @return true if a user with this email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user already exists with the given username.
     *
     * @param username The username to check.
     * @return true if a user with this username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user already exists with the given phone number (for phone registration/verification).
     * Assumes 'phoneNumber' is the exact property name in the User entity.
     *
     * @param mobilePhone The phone number to check.
     * @return true if a user with this phone number exists, false otherwise.
     */
    boolean existsByMobilePhone(String mobilePhone);

    // --- OAuth/External Provider Methods ---

    /**
     * Finds a user that was registered via an external authentication provider (OAuth).
     * This is essential for linking external accounts.
     *
     * @param authProvider The name of the provider (e.g., "google", "github").
     * @param providerId   The unique ID provided by the external service.
     * @return An {@link Optional} containing the user if found.
     */
    Optional<User> findByAuthProviderAndProviderId(String authProvider, String providerId);
}