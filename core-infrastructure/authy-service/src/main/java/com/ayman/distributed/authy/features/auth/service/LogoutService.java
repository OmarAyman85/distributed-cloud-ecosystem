package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.features.token.repository.TokenRepository;
import com.ayman.distributed.authy.features.token.model.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling user logout by revoking authentication tokens.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Logs out a user by revoking their refresh token from the cookie.
     *
     * @param request        The HTTP request containing the refresh token cookie.
     * @param response       The HTTP response to clear the cookie.
     * @param authentication The authentication object (not used in this implementation).
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Extract refresh token from cookie (not from Authorization header)
        String token = extractRefreshTokenFromCookie(request);

        if (token == null) {
            return; // No valid token found, nothing to process.
        }

        // Revoke the refresh token
        tokenRepository.findByToken(token).ifPresent(this::revokeToken);

        // Clear the refresh token cookie
        clearRefreshTokenCookie(response);
    }

    /**
     * Extracts the refresh token from the cookie.
     *
     * @param request The HTTP request containing cookies.
     * @return The refresh token if present, otherwise null.
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * Clears the refresh token cookie from the response.
     *
     * @param response The HTTP response.
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: Set to true in production
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);
    }

    /**
     * Marks a given token as revoked using the existing method from RefreshToken entity.
     *
     * @param token The token entity to be revoked.
     */
    private void revokeToken(RefreshToken token) {
        token.revoke(); // Use the existing revoke() method from the entity
        tokenRepository.save(token);
    }
}