package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.features.identity.model.*;
import com.ayman.distributed.authy.features.identity.repository.RoleRepository;
import com.ayman.distributed.authy.features.identity.repository.UserProviderRepository;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.identity.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Custom OAuth2 User Service to handle user creation/linking after social login.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String providerName = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        // Extract basic info (names vary by provider)
        String providerId = extractProviderId(providerName, attributes);
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = extractPicture(providerName, attributes);

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // 1. Check if provider account is already linked
        Optional<UserProvider> existingProvider = userProviderRepository.findByProviderNameAndProviderId(providerName, providerId);
        
        User user;
        if (existingProvider.isPresent()) {
            user = existingProvider.get().getUser();
            // Update profile fields if needed
            updateProfile(user, name, picture);
        } else {
            // 2. Check if user exists with this email but not linked yet
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // 3. Create new user
                user = createNewUser(email, name, picture, providerName);
            }
            
            // Link new provider to the user
            UserProvider newProvider = UserProvider.builder()
                    .providerName(providerName)
                    .providerId(providerId)
                    .user(user)
                    .build();
            userProviderRepository.save(newProvider);
        }

        return oauth2User; // We can wrap this in a more specific class if needed later
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        if ("GITHUB".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        return (String) attributes.get("sub"); // Google, etc.
    }

    private String extractPicture(String provider, Map<String, Object> attributes) {
        if ("GITHUB".equals(provider)) {
            return (String) attributes.get("avatar_url");
        }
        return (String) attributes.get("picture");
    }

    private User createNewUser(String email, String name, String picture, String provider) {
        String username = email.split("@")[0] + "_" + provider.toLowerCase();
        
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword("{noop}"); // Social users don't have passwords
        user.setFirstName(name != null ? name.split(" ")[0] : "Social");
        user.setLastName(name != null && name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "User");
        user.setDisplayName(name);
        user.setAvatarUrl(picture);
        user.setGender(Gender.OTHER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setAuthProvider(provider);
        
        final User savedUser = userRepository.save(user);

        // Assign default USER role if roles exist
        roleRepository.findByRoleKey("ROLE_USER").ifPresent(role -> {
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        });

        return savedUser;
    }

    private void updateProfile(User user, String name, String picture) {
        if (user.getDisplayName() == null && name != null) {
            user.setDisplayName(name);
        }
        if (user.getAvatarUrl() == null && picture != null) {
            user.setAvatarUrl(picture);
        }
        userRepository.save(user);
    }
}
