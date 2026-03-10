package com.ayman.distributed.savvy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * =============================================================================
 * ERROR RESPONSE DTO - Standardized Error Response Structure
 * =============================================================================
 * 
 * This Data Transfer Object (DTO) provides a consistent structure for error
 * responses across all API endpoints in the Savvy Expense Tracker application.
 * 
 * Using a standardized error format makes it easier for frontend applications
 * to parse and display error messages to users in a consistent way.
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@Data  // Lombok: Generates getters, setters, toString, equals, and hashCode
@AllArgsConstructor  // Lombok: Generates constructor with all fields
@NoArgsConstructor  // Lombok: Generates no-args constructor (required for JSON deserialization)
public class ErrorResponse {
    
    /**
     * Timestamp when the error occurred.
     * Helps with debugging and tracking when issues happen.
     * 
     * Example: "2026-01-28T15:30:45"
     */
    private LocalDateTime timestamp;
    
    /**
     * HTTP status code of the error.
     * 
     * Common values:
     * - 400: Bad Request (invalid input)
     * - 401: Unauthorized (authentication required)
     * - 403: Forbidden (insufficient permissions)
     * - 404: Not Found (resource doesn't exist)
     * - 500: Internal Server Error (unexpected server error)
     * 
     * Example: 404
     */
    private int status;
    
    /**
     * Short error name/type.
     * Provides a machine-readable error identifier.
     * 
     * Examples: "Not Found", "Bad Request", "Internal Server Error"
     */
    private String error;
    
    /**
     * Detailed human-readable error message.
     * Explains what went wrong and possibly how to fix it.
     * 
     * Example: "Expense not found with ID: 123. Please check the ID and try again."
     */
    private String message;
    
    /**
     * The API endpoint path where the error occurred.
     * Useful for debugging and logging.
     * 
     * Example: "/api/expense/123"
     */
    private String path;
    
    /**
     * Convenience constructor for creating error responses with just a message.
     * Automatically sets the timestamp to now.
     * 
     * @param status HTTP status code
     * @param error Error type/name
     * @param message Detailed error message
     * @param path API endpoint path
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
