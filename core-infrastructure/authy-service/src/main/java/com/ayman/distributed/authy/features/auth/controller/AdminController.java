package com.ayman.distributed.authy.features.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for administrative operations.
 * This controller is secured and accessible only by users with the 'ADMIN' authority.
 */
@Tag(name = "Admin", description = "Restricted endpoints for administrative tasks")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')") // Ensures only ADMIN users can access this controller
public class AdminController {

    /**
     * Handles GET requests for admin-related operations.
     * @return a response message indicating a successful GET request
     */
    @Operation(summary = "Admin GET demo", description = "A simple endpoint to test ADMIN access via GET")
    @GetMapping
    public String get() {
        return "GET::Admin Controller";
    }

    /**
     * Handles POST requests for creating admin-related resources.
     * @return a response message indicating a successful POST request
     */
    @Operation(summary = "Admin POST demo", description = "A simple endpoint to test ADMIN access via POST")
    @PostMapping
    public String post() {
        return "POST::Admin Controller";
    }

    /**
     * Handles PUT requests for updating admin-related resources.
     * @return a response message indicating a successful PUT request
     */
    @Operation(summary = "Admin PUT demo", description = "A simple endpoint to test ADMIN access via PUT")
    @PutMapping
    public String put() {
        return "PUT::Admin Controller";
    }

    /**
     * Handles DELETE requests for removing admin-related resources.
     * @return a response message indicating a successful DELETE request
     */
    @Operation(summary = "Admin DELETE demo", description = "A simple endpoint to test ADMIN access via DELETE")
    @DeleteMapping
    public String delete() {
        return "DELETE::Admin Controller";
    }
}
