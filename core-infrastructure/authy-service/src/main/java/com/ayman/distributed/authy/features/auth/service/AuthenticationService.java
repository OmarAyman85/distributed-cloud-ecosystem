package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.common.exception.InvalidCredentialsException;
import com.ayman.distributed.authy.features.auth.dto.AuthenticationResponse;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.auth.dto.VerificationRequest;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.identity.service.ApplicationService;
import com.ayman.distributed.authy.features.mfa.service.MfaService;
import com.ayman.distributed.authy.features.token.model.RefreshToken;
import com.ayman.distributed.authy.features.token.service.JwtService;
import com.ayman.distributed.authy.features.token.service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Core security service providing authentication, registration, and MFA verification flows.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MfaService mfaService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationService applicationService;
    private final PasswordEncoder passwordEncoder;
    private final com.ayman.distributed.authy.features.identity.mapper.UserMapper userMapper;
    private final com.ayman.distributed.authy.features.security.service.PasswordBreachService passwordBreachService;

    public AuthenticationResponse authenticate(UserLoginRequestDTO request) {
        User user = userRepository.findByIdentifier(request.identifier())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username/email/phone or password."));

        if (user.getLockTime() != null) {
            if (user.getLockTime().isAfter(java.time.LocalDateTime.now().minusMinutes(15))) {
                throw new org.springframework.security.authentication.LockedException("Account is locked due to too many failed attempts. Try again in 15 minutes.");
            } else {
                user.setLockTime(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
            );
            // Reset on success
            if (user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        } catch (AuthenticationException e) {
            // Increment failures
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockTime(java.time.LocalDateTime.now());
                log.warn("User {} account locked due to too many failed attempts.", user.getUsername());
            }
            userRepository.save(user);
            log.debug("Authentication failed for identifier [{}]: {}", request.identifier(), e.getMessage());
            throw new InvalidCredentialsException("Invalid username/email/phone or password.");
        }

        Application app = applicationService.findByAppKeyOrDefault(request.appKey());
        List<String> appRoles = applicationService.getAppRolesForUser(user, app.getAppKey());

        if (user.isMfaEnabled()) {
            String preAuthToken = jwtService.generatePreAuthToken(user.getUsername());
            return new AuthenticationResponse(preAuthToken, true, user.getUsername());
        }

        String accessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
        refreshTokenService.revokeAllUserRefreshTokens(user);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, app);

        return new AuthenticationResponse(accessToken, refreshTokenEntity.getToken(), user.getUsername());
    }

    public AuthenticationResponse register(StandardUserRegistrationRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Check for breached password
        if (passwordBreachService.isPasswordBreached(request.password())) {
            throw new com.ayman.distributed.authy.common.exception.WeakPasswordException("This password has appeared in a data breach and is unsafe to use.");
        }

        Application app = applicationService.findByAppKeyOrDefault(request.appKey());
        User user = userMapper.fromStandardRegistration(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        
        if (request.mfaEnabled()) {
            String mfaSecret = mfaService.generateNewSecret();
            user.setMfaSecret(mfaSecret);
        }

        userRepository.save(user);

        List<String> appRoles = applicationService.getAppRolesForUser(user, app.getAppKey());

        if (user.isMfaEnabled()) {
            String qrUri = mfaService.generateQRCode(user.getMfaSecret());
            String accessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, app);
            return new AuthenticationResponse(accessToken, refreshTokenEntity.getToken(), true, qrUri, user.getUsername());
        }

        String accessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, app);
        return new AuthenticationResponse(accessToken, refreshTokenEntity.getToken(), user.getUsername());
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshTokenString = getRefreshTokenFromCookie(request);
        if (refreshTokenString == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<RefreshToken> tokenOpt = refreshTokenService.findByToken(refreshTokenString);
        if (tokenOpt.isEmpty() || !tokenOpt.get().isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshToken refreshTokenEntity = tokenOpt.get();
        User user = refreshTokenEntity.getUser();

        if (!jwtService.isValidRefreshToken(refreshTokenString, user)
                || refreshTokenService.verifyExpiration(refreshTokenEntity) == null) {
            refreshTokenService.revokeToken(refreshTokenEntity);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Application app = refreshTokenEntity.getApplication();
        List<String> appRoles = applicationService.getAppRolesForUser(user, app.getAppKey());

        // Strictly enforce rotation: Mark old as used
        refreshTokenService.markUsed(refreshTokenEntity);
        
        String newAccessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
        RefreshToken newRefreshTokenEntity = refreshTokenService.createRefreshToken(user, app);

        Cookie cookie = new Cookie("refreshToken", newRefreshTokenEntity.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenService.getRefreshTokenDurationSeconds());
        response.addCookie(cookie);

        return ResponseEntity.ok(
                new AuthenticationResponse(
                        newAccessToken,
                        newRefreshTokenEntity.getToken(),
                        user.getUsername()
                )
        );
    }

    /**
     * Globally logs out the user by revoking the current access token and refresh token.
     */
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtService.revokeToken(token);
        }

        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenService.findByToken(refreshToken).ifPresent(refreshTokenService::revokeToken);
        }
    }

    public AuthenticationResponse verifyCode(VerificationRequest request) {
        if (request.getPreAuthToken() == null || !jwtService.isValidPreAuthToken(request.getPreAuthToken(), request.getUsername())) {
             throw new InvalidCredentialsException("Invalid or expired Pre-Auth token. Please login again.");
        }
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        boolean isValid = false;
        String code = request.getCode();
        
        if (code != null && code.length() == 6 && code.chars().allMatch(Character::isDigit)) {
            isValid = mfaService.isTotpValid(user.getMfaSecret(), code);
        } else {
            isValid = mfaService.verifyRecoveryCode(user, code);
        }

        if (!isValid) {
            throw new InvalidCredentialsException("Invalid verification code");
        }

        Application app = applicationService.findByAppKeyOrDefault(request.getAppKey());
        List<String> appRoles = applicationService.getAppRolesForUser(user, app.getAppKey());

        String accessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
        refreshTokenService.revokeAllUserRefreshTokens(user);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, app);

        return new AuthenticationResponse(
                accessToken,
                refreshTokenEntity.getToken(),
                user.getUsername()
        );
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
