package com.ayman.distributed.authy.features.auth.service;

import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.identity.service.ApplicationService;
import com.ayman.distributed.authy.features.token.model.RefreshToken;
import com.ayman.distributed.authy.features.token.service.JwtService;
import com.ayman.distributed.authy.features.token.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Handles successful OAuth2 authentication by generating JWT tokens and redirecting.
 */
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServletException("User not found after social login"));

        // Default to "AUTHY" app for social login redirects if not specified
        Application app = applicationService.findByAppKeyOrDefault("AUTHY");
        List<String> appRoles = applicationService.getAppRolesForUser(user, app.getAppKey());

        // Generate tokens
        String accessToken = jwtService.generateToken(user, app.getAppKey(), appRoles);
        refreshTokenService.revokeAllUserRefreshTokens(user);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, app);

        // Set Refresh Token in Cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshTokenEntity.getToken())
                .httpOnly(true)
                .secure(false) // true in prod
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // Redirect to frontend with access token
        // In a real scenario, this would be a frontend URL (e.g., http://localhost:3000/auth-success)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api")
                .queryParam("token", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
