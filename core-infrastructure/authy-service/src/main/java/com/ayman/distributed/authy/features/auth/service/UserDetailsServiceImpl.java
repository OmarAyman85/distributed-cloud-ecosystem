package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} to load user details from the database.
 * This is used by Spring Security for authentication and authorization.
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by username.
     *
     * @param username The username of the user to be loaded.
     * @return The {@link UserDetails} object containing user information.
     * @throws UsernameNotFoundException If no user is found with the provided username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username, username)
                .map(User.class::cast) // Ensures the return type matches UserDetails
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
