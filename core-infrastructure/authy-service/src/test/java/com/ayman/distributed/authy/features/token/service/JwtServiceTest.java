package com.ayman.distributed.authy.features.token.service;

import com.ayman.distributed.authy.features.token.model.RevokedToken;
import com.ayman.distributed.authy.features.token.repository.RevokedTokenRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @InjectMocks
    private JwtService jwtService;

    private final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Mock secret
    private final long EXPIRATION = 3600000; // 1 hour

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", EXPIRATION * 24);

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertEquals("testuser", jwtService.getUsernameFromToken(token));
    }

    @Test
    void shouldGenerateTokenWithAppKeyAndRoles() {
        String appKey = "TEST_APP";
        List<String> roles = List.of("ADMIN", "USER");
        
        String token = jwtService.generateToken(userDetails, appKey, roles);
        
        assertEquals(appKey, jwtService.extractAppKey(token));
        assertEquals(roles, jwtService.extractRoles(token));
    }

    @Test
    void shouldValidateCorrectToken() {
        String token = jwtService.generateToken(userDetails);
        when(revokedTokenRepository.findByJti(anyString())).thenReturn(Optional.empty());
        
        assertTrue(jwtService.isValidToken(token, userDetails));
    }

    @Test
    void shouldFailValidationIfTokenRevoked() {
        String token = jwtService.generateToken(userDetails);
        when(revokedTokenRepository.findByJti(anyString())).thenReturn(Optional.of(new RevokedToken()));
        
        assertFalse(jwtService.isValidToken(token, userDetails));
    }

    @Test
    void shouldExtractJti() {
        String token = jwtService.generateToken(userDetails);
        String jti = jwtService.extractClaim(token, Claims::getId);
        assertNotNull(jti);
    }

    @Test
    void shouldHandlePreAuthTokensCorrectly() {
        String preAuthToken = jwtService.generatePreAuthToken("testuser");
        assertNotNull(preAuthToken);
        assertTrue(jwtService.isValidPreAuthToken(preAuthToken, "testuser"));
    }
}
