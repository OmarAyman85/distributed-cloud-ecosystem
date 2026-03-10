package com.ayman.distributed.authy.features.identity.service;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;

import com.ayman.distributed.authy.features.identity.dto.*;
import com.ayman.distributed.authy.common.exception.*;
import com.ayman.distributed.authy.features.identity.mapper.UserMapper;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.model.UserStatus;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserService} providing business logic for user management.
 * Handles registration for various authentication providers, user retrieval, 
 * updates, and status changes.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user using standard email/password credentials.
     * @param dto Registration details.
     * @return The registered user's details as a DTO.
     * @throws EmailAlreadyExistsException if the email is already taken.
     * @throws UsernameAlreadyExistsException if the username is already taken.
     */
    @Override
    public UserResponseDTO registerStandardUser(StandardUserRegistrationRequestDTO dto) {
        if (dto.email() != null && userRepository.existsByEmail(dto.email()))
            throw new EmailAlreadyExistsException(dto.email());

        if (dto.username() != null && userRepository.existsByUsername(dto.username()))
            throw new UsernameAlreadyExistsException(dto.username());

        User user = userMapper.fromStandardRegistration(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        // Default status for standard users is ACTIVE
        user.setStatus(UserStatus.ACTIVE);
        
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    /**
     * Registers a user authenticated via an OAuth2 provider (e.g., Google, GitHub).
     * @param dto OAuth user details from the provider.
     * @return The registered user's details as a DTO.
     */
    @Override
    public UserResponseDTO registerOAuthUser(OAuthUserRegistrationRequestDTO dto) {
        if (dto.email() != null && userRepository.existsByEmail(dto.email()))
            throw new EmailAlreadyExistsException(dto.email());
        if (dto.username() != null && userRepository.existsByUsername(dto.username()))
            throw new UsernameAlreadyExistsException(dto.username());

        User user = userMapper.fromOAuthRegistration(dto);
        
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    /**
     * Registers a user using a mobile phone number.
     * Initial status is set to PENDING_VERIFICATION until the phone is verified.
     * @param dto Phone registration details.
     * @return The registered user's details as a DTO.
     */
    @Override
    public UserResponseDTO registerPhoneUser(PhoneUserRegistrationRequestDTO dto) {
        if (dto.mobilePhone() != null && userRepository.existsByMobilePhone(dto.mobilePhone()))
            throw new PhoneAlreadyExistsException(dto.mobilePhone());

        User user = userMapper.fromPhoneRegistration(dto);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    /**
     * Authenticates a user based on identifier (email/username/phone) and password.
     * Useful for internal lookups or simple authentication flows.
     * @param dto Login credentials.
     * @return User details if authentication succeeds.
     */
    @Override
    public UserResponseDTO login(UserLoginRequestDTO dto) {
        User user = userRepository.findByUsernameOrEmail(dto.identifier(), dto.identifier())
                .orElseThrow(() -> new UserNotFoundException("Invalid username/email"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new InvalidCredentialsException("Invalid username/email/phone or password.");

        return userMapper.toResponse(user);
    }

    /**
     * Retrieves a user by their unique UUID.
     * @param id The user's ID.
     * @return User details.
     */
    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    /**
     * Searches for a user by either their username or email address.
     * @param usernameOrEmail The search identifier.
     * @return User details.
     */
    @Override
    public UserResponseDTO getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + usernameOrEmail));
        return userMapper.toResponse(user);
    }

    /**
     * Updates an existing user's information.
     * @param id The user's ID.
     * @param dto Updated information.
     * @return The updated user's details.
     */
    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userMapper.updateUserFromDto(dto, user);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    /**
     * Deletes a user from the system.
     * @param id The ID of the user to delete.
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException("User not found with id: " + id);
        userRepository.deleteById(id);
    }

    /**
     * Sets a user's status to ACTIVE.
     * @param id The user's ID.
     */
    @Override
    public void activateUser(Long id) {
        changeUserStatus(id, UserStatus.ACTIVE);
    }

    /**
     * Sets a user's status to DEACTIVATED, preventing login.
     * @param id The user's ID.
     */
    @Override
    public void deactivateUser(Long id) {
        changeUserStatus(id, UserStatus.DEACTIVATED);
    }

    /**
     * Internal helper to change a user's status.
     * @param id User ID.
     * @param status New status.
     */
    private void changeUserStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setStatus(status);
        userRepository.save(user);
    }

    /**
     * Retrieves a user by their authentication provider and provider-specific ID.
     * Useful for OAuth2 login flows.
     */
    @Override
    public User getByAuthProviderAndProviderId(String authProvider, String providerId) {
        return userRepository.findByAuthProviderAndProviderId(authProvider, providerId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found for provider " + authProvider + " and providerId " + providerId));
    }

    /**
     * Retrieves detailed user information by username.
     * @param username The username.
     * @return User details.
     */
    public UserResponseDTO getUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }
}
