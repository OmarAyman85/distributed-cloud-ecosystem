package com.ayman.distributed.authy.common.config;

import com.ayman.distributed.authy.features.auth.service.CustomOAuth2SuccessHandler;
import com.ayman.distributed.authy.features.auth.service.CustomOAuth2UserService;
import com.ayman.distributed.authy.features.auth.service.UserDetailsServiceImpl;
import com.ayman.distributed.authy.features.token.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Security Configuration for the Authy Service.
 *
 * This class configures the security filter chain, authentication providers, and HTTP security settings.
 * It enables stateless session management (JWT-based), disables CSRF (as we use tokens), and configures
 * the public and protected endpoints.
 *
 * Key Features:
 * - JWT Authentication Filter specific integration.
 * - Stateless Session Policy.
 * - Public endpoints for Auth (Login, Register, Refresh).
 * - Secured endpoints requiring valid authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutHandler logoutHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    /**
     * Configures the main SecurityFilterChain.
     * 
     * CORE CONCEPT:
     * Spring Security works as a "Chain of Filters". When a request comes in, 
     * it must pass through several checkpoints (Auth checks, role checks).
     * 
     * We use STATELESS sessions because we use JWTs. This means the server 
     * doesn't "remember" you via a session ID; you must send your token 
     * with EVERY single request.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/api",
                                        "/api/auth/login",
                                        "/api/auth/register",
                                        "/oauth2/**",
                                        "/api/auth/refresh-token",
                                        "/api/upload-profile-picture",
                                        "/api/user/me",
                                        "/uploads/**",
                                        "/api/verify-code",
                                        "/api/magic-link/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                )
                                .permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(customOAuth2SuccessHandler)
                )
                .userDetailsService(userDetailsService)
                /* 
                 * We add our custom JWT filter BEFORE the standard UsernamePasswordAuthenticationFilter.
                 * This ensures we check for a valid JWT token first.
                 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                        .logoutSuccessUrl("/api")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
