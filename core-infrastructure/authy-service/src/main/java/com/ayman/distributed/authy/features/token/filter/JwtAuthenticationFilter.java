package com.ayman.distributed.authy.features.token.filter;

import com.ayman.distributed.authy.features.token.service.JwtService;
import com.ayman.distributed.authy.features.auth.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication filter that intercepts requests to authenticate users based on JWT tokens.
 * This filter validates access tokens (JWTs) from the Authorization header.
 *
 * Note: Access tokens are stateless and cannot be revoked. They are valid until expiration.
 * Refresh tokens (stored in cookies) can be revoked and are checked during token refresh.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Performs filtering to authenticate users based on JWT access tokens.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException      if an input/output error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        // Proceed with the request if no valid Bearer token is found
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token from header (after "Bearer ")
            String token = authHeader.substring(7);
            String username = jwtService.getUsernameFromToken(token);

            // Validate token and authenticate user if SecurityContext is empty
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Verify JWT token is valid (signature, expiration, etc.)
                // Note: Access tokens (JWTs) are stateless and cannot be revoked server-side
                // They remain valid until they expire naturally
                if (jwtService.isValidToken(token, userDetails)) {
                    // Create authentication token and set details
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log the error but don't break the filter chain
            logger.error("Cannot set user authentication: {}", e);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}