package com.ayman.distributed.authy.features.identity.service;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;

import com.ayman.distributed.authy.features.identity.dto.*;
import com.ayman.distributed.authy.features.identity.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    /* ----------- Registration ----------- */

    UserResponseDTO registerStandardUser(StandardUserRegistrationRequestDTO dto);

    UserResponseDTO registerOAuthUser(OAuthUserRegistrationRequestDTO dto);

    UserResponseDTO registerPhoneUser(PhoneUserRegistrationRequestDTO dto);

    /* ----------- Authentication ----------- */

    UserResponseDTO login(UserLoginRequestDTO dto);

    /* ----------- Retrieval ----------- */

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsernameOrEmail(String usernameOrEmail);

    User getByAuthProviderAndProviderId(String authProvider, String providerId);

    UserResponseDTO getUserDetails(String username);

    /* ----------- Update ----------- */

    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto);

    /* ----------- Deletion / Status ----------- */

    void deleteUser(Long id);

    void activateUser(Long id);

    void deactivateUser(Long id);
}
