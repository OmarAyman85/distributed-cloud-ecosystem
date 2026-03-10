package com.ayman.distributed.savvy.exception;

import com.ayman.distributed.savvy.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * =============================================================================
 * GLOBAL EXCEPTION HANDLER - Centralized Error Handling
 * =============================================================================
 * 
 * This class provides centralized exception handling for the entire Savvy
 * Expense Tracker application. It intercepts exceptions thrown by controllers
 * and converts them into standardized error responses.
 * 
 * Benefits of centralized exception handling:
 * - Consistent error response format across all endpoints
 * - Eliminates repetitive try-catch blocks in controllers
 * - Easier to maintain and update error handling logic
 * - Better separation of concerns (controllers focus on business logic)
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@RestControllerAdvice  // Makes this class handle exceptions for all @RestController classes
public class GlobalExceptionHandler {

    // =========================================================================
    // ENTITY NOT FOUND EXCEPTION (HTTP 404)
    // =========================================================================
    
    /**
     * Handles EntityNotFoundException thrown when a requested resource doesn't exist.
     * 
     * This exception is typically thrown when:
     * - Trying to get an expense/income by ID that doesn't exist
     * - Trying to update a non-existent record
     * - Trying to delete a non-existent record
     * 
     * @param ex The EntityNotFoundException that was thrown
     * @param request The HTTP request that caused the exception
     * @return ResponseEntity with HTTP 404 (Not Found) and error details
     * 
     * @example
     * When GET /api/expense/999 is called and expense 999 doesn't exist:
     * Response: {
     *   "timestamp": "2026-01-28T15:30:45",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "Expense not found with ID: 999",
     *   "path": "/api/expense/999"
     * }
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        
        // Create a standardized error response
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),  // 404
                "Not Found",
                ex.getMessage(),  // The custom message from the exception
                request.getRequestURI()  // The endpoint that was called
        );
        
        // Return HTTP 404 with the error details
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // =========================================================================
    // VALIDATION EXCEPTION (HTTP 400)
    // =========================================================================
    
    /**
     * Handles MethodArgumentNotValidException thrown when request body validation fails.
     * 
     * This exception is thrown when:
     * - A DTO with @Valid annotation fails validation
     * - Required fields are missing
     * - Field values don't meet validation constraints (@NotNull, @Min, @Max, etc.)
     * 
     * @param ex The MethodArgumentNotValidException that was thrown
     * @param request The HTTP request that caused the exception
     * @return ResponseEntity with HTTP 400 (Bad Request) and validation error details
     * 
     * @example
     * When POST /api/expense with invalid data (e.g., negative amount):
     * Response: {
     *   "timestamp": "2026-01-28T15:30:45",
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "message": "Input validation failed. See 'errors' for details.",
     *   "path": "/api/expense",
     *   "errors": {
     *     "amount": "Amount must be positive",
     *     "title": "Title is required"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        // Create a map to hold validation errors for each field
        Map<String, String> validationErrors = new HashMap<>();
        
        // Extract all field errors from the exception
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        // Create the error response structure
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", java.time.LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Input validation failed. See 'errors' for details.");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("errors", validationErrors);  // Field-specific errors
        
        // Return HTTP 400 with validation error details
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // =========================================================================
    // ILLEGAL ARGUMENT EXCEPTION (HTTP 400)
    // =========================================================================
    
    /**
     * Handles IllegalArgumentException thrown when invalid arguments are provided.
     * 
     * This exception is typically thrown when:
     * - Invalid enum values are provided
     * - Business logic constraints are violated
     * - Invalid parameter combinations are used
     * 
     * @param ex The IllegalArgumentException that was thrown
     * @param request The HTTP request that caused the exception
     * @return ResponseEntity with HTTP 400 (Bad Request) and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),  // 400
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // =========================================================================
    // GENERIC EXCEPTION (HTTP 500)
    // =========================================================================
    
    /**
     * Handles all other uncaught exceptions.
     * 
     * This is a catch-all handler for any exceptions not specifically handled above.
     * It prevents the application from exposing internal error details to clients.
     * 
     * @param ex The Exception that was thrown
     * @param request The HTTP request that caused the exception
     * @return ResponseEntity with HTTP 500 (Internal Server Error) and generic error message
     * 
     * @apiNote In production, this should log the full exception stack trace for debugging
     *          while only returning a generic message to the client for security.
     * 
     * @example
     * When an unexpected database error occurs:
     * Response: {
     *   "timestamp": "2026-01-28T15:30:45",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "message": "An unexpected error occurred. Please try again later.",
     *   "path": "/api/expense"
     * }
     */
    // =========================================================================
    // AUTHENTICATION EXCEPTION (HTTP 401)
    // =========================================================================

    /**
     * Handles BadCredentialsException thrown when login fails.
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),  // 401
                "Unauthorized",
                "Invalid username or password",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        // TODO: Add proper logging here
        // logger.error("Unexpected error occurred", ex);
        
        // Print stack trace for debugging (remove in production)
        ex.printStackTrace();
        
        // Create a generic error response (don't expose internal details)
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // 500
                "Internal Server Error",
                "DEBUG: " + ex.getMessage() + " | Cause: " + (ex.getCause() != null ? ex.getCause().getMessage() : "null"), // Expose for debugging
                request.getRequestURI()
        );
        
        // Return HTTP 500 with generic error message
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // =========================================================================
    // FUTURE ENHANCEMENTS
    // =========================================================================
    
    // TODO: Add handler for AccessDeniedException (HTTP 403)
    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ErrorResponse> handleAccessDeniedException(...)
    
    // TODO: Add handler for AuthenticationException (HTTP 401)
    // @ExceptionHandler(AuthenticationException.class)
    // public ResponseEntity<ErrorResponse> handleAuthenticationException(...)
    
    // TODO: Add handler for DataIntegrityViolationException (database constraints)
    // @ExceptionHandler(DataIntegrityViolationException.class)
    // public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(...)
    
    // TODO: Add handler for custom business exceptions
    // @ExceptionHandler(InsufficientBalanceException.class)
    // public ResponseEntity<ErrorResponse> handleInsufficientBalance(...)
}
