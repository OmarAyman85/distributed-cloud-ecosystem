package com.ayman.distributed.savvy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * =============================================================================
 * CORS CONFIGURATION - Cross-Origin Resource Sharing Setup
 * =============================================================================
 * 
 * This configuration class centralizes CORS (Cross-Origin Resource Sharing)
 * settings for the entire Savvy Expense Tracker application.
 * 
 * CORS allows the frontend application (running on a different origin/port)
 * to make HTTP requests to this backend API.
 * 
 * Benefits of centralized CORS configuration:
 * - Single source of truth for CORS settings
 * - Eliminates need for @CrossOrigin annotations on every controller
 * - Easier to maintain and update
 * - Environment-specific configuration via properties
 * 
 * Configuration is loaded from application.properties:
 * - spring.web.cors.allowed-origins (from environment variable)
 * - spring.web.cors.allowed-methods
 * - spring.web.cors.allowed-headers
 * - spring.web.cors.allow-credentials
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@Configuration  // Marks this class as a Spring configuration class
public class CorsConfig {

    /**
     * Allowed origins for CORS requests.
     * Loaded from application.properties: spring.web.cors.allowed-origins
     * 
     * Example values:
     * - Development: http://localhost:4200
     * - Production: https://yourdomain.com
     * - Multiple: http://localhost:4200,https://yourdomain.com
     */
    @Value("${spring.web.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Allowed HTTP methods for CORS requests.
     * Loaded from application.properties: spring.web.cors.allowed-methods
     * 
     * Default: GET,POST,PUT,DELETE,OPTIONS
     */
    @Value("${spring.web.cors.allowed-methods}")
    private String allowedMethods;

    /**
     * Allowed headers for CORS requests.
     * Loaded from application.properties: spring.web.cors.allowed-headers
     * 
     * Default: * (all headers)
     */
    @Value("${spring.web.cors.allowed-headers}")
    private String allowedHeaders;

    /**
     * Whether to allow credentials (cookies, authorization headers) in CORS requests.
     * Loaded from application.properties: spring.web.cors.allow-credentials
     * 
     * Default: true
     */
    @Value("${spring.web.cors.allow-credentials}")
    private boolean allowCredentials;

    /**
     * Configures CORS settings for the application.
     * 
     * This bean is automatically picked up by Spring Security and applied
     * to all HTTP requests.
     * 
     * CORS Flow:
     * 1. Browser sends preflight OPTIONS request (for non-simple requests)
     * 2. Server responds with allowed origins, methods, headers
     * 3. If allowed, browser sends actual request
     * 4. Server processes request and returns response
     * 
     * @return CorsConfigurationSource with configured CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Create CORS configuration object
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins (split comma-separated string into list)
        // Example: "http://localhost:4200,https://yourdomain.com" -> ["http://localhost:4200", "https://yourdomain.com"]
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);

        // Set allowed HTTP methods
        // Example: "GET,POST,PUT,DELETE,OPTIONS" -> ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);

        // Set allowed headers
        // "*" means all headers are allowed
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        configuration.setAllowedHeaders(headers);

        // Allow credentials (cookies, authorization headers)
        // Required for JWT token authentication
        configuration.setAllowCredentials(allowCredentials);

        // Expose headers that the frontend can access
        // Useful for custom headers like X-Total-Count for pagination
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // Cache preflight response for 1 hour (3600 seconds)
        // Reduces number of OPTIONS requests from browser
        configuration.setMaxAge(3600L);

        // Register CORS configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply to all paths

        return source;
    }

    // =========================================================================
    // FUTURE ENHANCEMENTS
    // =========================================================================

    // TODO: Add different CORS configurations for different environments
    // @Profile("production")
    // @Bean
    // public CorsConfigurationSource productionCorsConfig() { ... }

    // TODO: Add more restrictive CORS for sensitive endpoints
    // source.registerCorsConfiguration("/api/admin/**", restrictiveConfig);
}
