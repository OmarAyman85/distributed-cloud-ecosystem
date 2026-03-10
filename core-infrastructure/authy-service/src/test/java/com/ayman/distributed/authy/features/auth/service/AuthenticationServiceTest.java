package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.features.auth.dto.AuthenticationResponse;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.auth.dto.VerificationRequest;
import com.ayman.distributed.authy.features.identity.mapper.UserMapper;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.identity.service.ApplicationService;
import com.ayman.distributed.authy.features.mfa.service.MfaService;
import com.ayman.distributed.authy.features.token.model.RefreshToken;
import com.ayman.distributed.authy.features.token.service.JwtService;
import com.ayman.distributed.authy.features.token.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import com.ayman.distributed.authy.common.exception.InvalidCredentialsException;
import com.ayman.distributed.authy.common.exception.WeakPasswordException;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private MfaService mfaService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private ApplicationService applicationService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock
    private com.ayman.distributed.authy.features.security.service.PasswordBreachService passwordBreachService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private Application app;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setMfaEnabled(false);

        app = new Application();
        app.setAppKey("AUTHY");
    }

    @Test
    void register_ShouldThrowException_WhenPasswordIsBreached() {
        // Arrange
        StandardUserRegistrationRequestDTO request = new StandardUserRegistrationRequestDTO(
                "client-app", "test@example.com", "testuser", "password123", "Test", "User", null, null, null, null, null, null, null, false
        );
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordBreachService.isPasswordBreached(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(WeakPasswordException.class, () -> authenticationService.register(request));
    }

    @Test
    void authenticate_ShouldReturnTokens_WhenNoMfa() {
        UserLoginRequestDTO request = new UserLoginRequestDTO("AUTHY", "testuser", "password");
        
        when(userRepository.findByIdentifier("testuser")).thenReturn(Optional.of(user));
        when(applicationService.findByAppKeyOrDefault("AUTHY")).thenReturn(app);
        when(jwtService.generateToken(any(), eq("AUTHY"), any())).thenReturn("access-token");
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        when(refreshTokenService.createRefreshToken(any(), any())).thenReturn(refreshToken);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertFalse(response.isMfaRequired());
    }

    @Test
    void authenticate_ShouldReturnPreAuth_WhenMfaEnabled() {
        user.setMfaEnabled(true);
        UserLoginRequestDTO request = new UserLoginRequestDTO("AUTHY", "testuser", "password");
        
        when(userRepository.findByIdentifier("testuser")).thenReturn(Optional.of(user));
        when(applicationService.findByAppKeyOrDefault("AUTHY")).thenReturn(app);
        when(jwtService.generatePreAuthToken("testuser")).thenReturn("pre-auth-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertTrue(response.isMfaRequired());
        assertEquals("pre-auth-token", response.getPreAuthToken());
    }

    @Test
    void logout_ShouldRevokeTokens() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer access-token");
        
        authenticationService.logout(request);

        verify(jwtService).revokeToken("access-token");
    }

    @Test
    void authenticate_ShouldLockAccount_AfterMaxFailedAttempts() {
        // Arrange
        UserLoginRequestDTO request = new UserLoginRequestDTO("AUTHY", "testuser", "wrongpassword");
        User user = new User();
        user.setUsername("testuser");
        user.setFailedLoginAttempts(4); // One more failure locks it

        when(userRepository.findByIdentifier("testuser")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.authenticate(request));

        // Verify lock
        assertEquals(5, user.getFailedLoginAttempts());
        assertNotNull(user.getLockTime());
        verify(userRepository, times(1)).save(user); // Logic in service calls save once per failure catch
    }

    @Test
    void authenticate_ShouldThrowLockedException_WhenAccountIsLocked() {
        // Arrange
        UserLoginRequestDTO request = new UserLoginRequestDTO("AUTHY", "testuser", "password");
        User user = new User();
        user.setUsername("testuser");
        user.setLockTime(java.time.LocalDateTime.now().minusMinutes(10)); // Locked 10 mins ago (policy is 15)

        when(userRepository.findByIdentifier("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(org.springframework.security.authentication.LockedException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void verifyCode_ShouldReturnTokens_WhenOtpValid() {
        user.setMfaEnabled(true);
        user.setMfaSecret("secret");
        VerificationRequest request = new VerificationRequest("AUTHY", "testuser", "123456", "pre-auth-token");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.isValidPreAuthToken("pre-auth-token", "testuser")).thenReturn(true);
        when(mfaService.isTotpValid("secret", "123456")).thenReturn(true);
        when(applicationService.findByAppKeyOrDefault("AUTHY")).thenReturn(app);
        when(jwtService.generateToken(any(), eq("AUTHY"), any())).thenReturn("access-token");
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        when(refreshTokenService.createRefreshToken(any(), any())).thenReturn(refreshToken);

        AuthenticationResponse response = authenticationService.verifyCode(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
    }
}
