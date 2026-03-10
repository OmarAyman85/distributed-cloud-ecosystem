package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.dto.IncomeDTO;
import com.ayman.distributed.savvy.model.entity.Income;
import com.ayman.distributed.savvy.services.income.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * =============================================================================
 * INCOME CONTROLLER - Income Management API
 * =============================================================================
 * 
 * This REST controller handles all CRUD (Create, Read, Update, Delete) operations
 * for income transactions in the Savvy Expense Tracker application.
 * 
 * Income represents money earned/received by the user and is categorized for better
 * financial tracking and analysis.
 * 
 * Base URL: /api/income
 * 
 * Endpoints:
 * - POST   /api/income        - Create a new income
 * - GET    /api/income/all    - Get all incomes
 * - GET    /api/income/{id}   - Get a specific income by ID
 * - PUT    /api/income/{id}   - Update an existing income
 * - DELETE /api/income/{id}   - Delete an income
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@RestController  // Marks this class as a Spring MVC REST Controller (handles HTTP requests)
@RequestMapping("/api/income")  // Defines the base URL path for all endpoints in this controller
@RequiredArgsConstructor  // Lombok annotation to generate a constructor with final fields (Dependency Injection)
@Slf4j  // Lombok: Generates SLF4J logger instance (log variable)
@Tag(name = "Income", description = "Income management endpoints")
public class IncomeController {
    
    // =========================================================================
    // DEPENDENCIES
    // =========================================================================
    
    /**
     * Service layer for income-related business logic.
     * Injected automatically by Spring via constructor injection (thanks to @RequiredArgsConstructor).
     */
    private final IncomeService incomeService;

    // =========================================================================
    // CREATE OPERATIONS
    // =========================================================================
    
    /**
     * POST /api/income
     * 
     * Creates a new income transaction in the system.
     * 
     * This endpoint accepts income details (title, description, amount, category, date)
     * and persists them to the database. The created income is returned in the response.
     * 
     * @param incomeDTO Data Transfer Object containing income details
     *                  - title: Name/title of the income (e.g., "Monthly Salary")
     *                  - description: Detailed description of the income source
     *                  - amount: Amount received (in the user's currency)
     *                  - category: Category of income (e.g., "Salary", "Freelance", "Investment")
     *                  - date: Date when the income was received
     * 
     * @return ResponseEntity with HTTP 201 (Created) and the created Income object if successful,
     *         or HTTP 400 (Bad Request) if the income creation fails
     * 
     * @apiNote Validation is performed on the IncomeDTO to ensure data integrity
     * 
     * @example
     * POST http://localhost:8080/api/income
     * Body: {
     *   "title": "Monthly Salary",
     *   "description": "January 2026 salary",
     *   "amount": 5000,
     *   "category": "Salary",
     *   "date": "2026-01-28"
     * }
     */
    @PostMapping  // Maps this method to an HTTP POST request (URL: /api/income)
    @Operation(
        summary = "Create a new income",
        description = "Creates a new income transaction with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Income created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid income data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createIncome(
            @Valid @RequestBody @Parameter(description = "Income details") IncomeDTO incomeDTO) {
        
        log.info("Creating new income: {}", incomeDTO.getTitle());
        
        // Call service layer to create the income
        // Service layer handles business logic and database operations
        Income createdIncome = incomeService.createIncome(incomeDTO);
        
        if (createdIncome != null) {
            log.info("Income created successfully with ID: {}", createdIncome.getId());
            // If income was successfully created, return HTTP 201 (Created) with the new income data
            return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);
        } else {
            log.warn("Failed to create income: {}", incomeDTO.getTitle());
            // If income creation failed, return HTTP 400 (Bad Request)
            // TODO: Return a more descriptive error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create income. Please check your input data.");
        }
    }

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================
    
    /**
     * GET /api/income/all
     * 
     * Retrieves all income transactions from the database.
     * 
     * This endpoint returns a list of all incomes recorded by the user,
     * ordered by date (most recent first, typically).
     * 
     * @return ResponseEntity with HTTP 200 (OK) and a list of all Income objects
     * 
     * @apiNote For large datasets, consider implementing pagination to improve performance
     * 
     * @example
     * GET http://localhost:8080/api/income/all
     * Response: [
     *   { "id": 1, "title": "Salary", "amount": 5000, ... },
     *   { "id": 2, "title": "Freelance", "amount": 1000, ... }
     * ]
     */
    @GetMapping("/all")
    @Operation(
        summary = "Get all incomes",
        description = "Retrieves a list of all income transactions"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Incomes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllIncomes() {
        // Delegate to service layer to fetch all incomes
        // TODO: Add pagination support for better performance with large datasets
        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    /**
     * GET /api/income/{id}
     * 
     * Retrieves a specific income transaction by its unique ID.
     * 
     * This endpoint is useful for viewing detailed information about a single income,
     * or for populating an edit form with existing income data.
     * 
     * @param id The unique identifier of the income to retrieve
     * 
     * @return ResponseEntity with:
     *         - HTTP 200 (OK) and the Income object if found
     *         - HTTP 404 (Not Found) if the income doesn't exist
     *         - HTTP 500 (Internal Server Error) for unexpected errors
     * 
     * @throws EntityNotFoundException if no income exists with the given ID
     * 
     * @example
     * GET http://localhost:8080/api/income/1
     * Response: { "id": 1, "title": "Salary", "amount": 5000, ... }
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get income by ID",
        description = "Retrieves a specific income transaction by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Income found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Income not found with the given ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getIncomeById(
            @PathVariable @Parameter(description = "Income ID") Long id) {
        try {
            // Attempt to retrieve the income by ID
            Income income = incomeService.getIncomeById(id);
            return ResponseEntity.ok(income);
            
        } catch (EntityNotFoundException ex) {
            // Handle case where income with given ID doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Income not found with ID: " + id);
                    
        } catch (Exception e) {
            // Handle any unexpected errors
            // TODO: Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the income. Please try again later.");
        }
    }

    // =========================================================================
    // UPDATE OPERATIONS
    // =========================================================================
    
    /**
     * PUT /api/income/{id}
     * 
     * Updates an existing income transaction with new data.
     * 
     * This endpoint allows users to modify any field of an existing income
     * (title, description, amount, category, date).
     * 
     * @param id The unique identifier of the income to update
     * @param incomeDTO Data Transfer Object containing the updated income details
     * 
     * @return ResponseEntity with:
     *         - HTTP 200 (OK) and the updated Income object if successful
     *         - HTTP 404 (Not Found) if the income doesn't exist
     *         - HTTP 500 (Internal Server Error) for unexpected errors
     * 
     * @throws EntityNotFoundException if no income exists with the given ID
     * 
     * @example
     * PUT http://localhost:8080/api/income/1
     * Body: {
     *   "title": "Monthly Salary (Updated)",
     *   "description": "January 2026 salary with bonus",
     *   "amount": 5500,
     *   "category": "Salary",
     *   "date": "2026-01-28"
     * }
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an income",
        description = "Updates an existing income transaction with new details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Income updated successfully"),
        @ApiResponse(responseCode = "404", description = "Income not found with the given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid income data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateIncome(
            @PathVariable @Parameter(description = "Income ID") Long id,
            @Valid @RequestBody @Parameter(description = "Updated income details") IncomeDTO incomeDTO) {
        try {
            // Attempt to update the income
            Income updatedIncome = incomeService.updateIncome(incomeDTO, id);
            return ResponseEntity.ok(updatedIncome);
            
        } catch (EntityNotFoundException ex) {
            // Handle case where income with given ID doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot update: Income not found with ID: " + id);
                    
        } catch (Exception e) {
            // Handle any unexpected errors
            // TODO: Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the income. Please try again later.");
        }
    }

    // =========================================================================
    // DELETE OPERATIONS
    // =========================================================================
    
    /**
     * DELETE /api/income/{id}
     * 
     * Deletes an income transaction from the database.
     * 
     * This is a permanent operation and cannot be undone. The income record
     * will be completely removed from the system.
     * 
     * @param id The unique identifier of the income to delete
     * 
     * @return ResponseEntity with:
     *         - HTTP 200 (OK) if deletion was successful
     *         - HTTP 404 (Not Found) if the income doesn't exist
     *         - HTTP 500 (Internal Server Error) for unexpected errors
     * 
     * @throws EntityNotFoundException if no income exists with the given ID
     * 
     * @apiNote Consider implementing soft delete (marking as deleted instead of removing)
     *          to allow for data recovery and audit trails
     * 
     * @example
     * DELETE http://localhost:8080/api/income/1
     * Response: HTTP 200 OK
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an income",
        description = "Permanently deletes an income transaction from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Income deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Income not found with the given ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteIncome(
            @PathVariable @Parameter(description = "Income ID") Long id) {
        try {
            // Attempt to delete the income
            incomeService.deleteIncome(id);
            
            // Return success response
            // TODO: Consider returning a success message instead of null
            return ResponseEntity.ok("Income deleted successfully");
            
        } catch (EntityNotFoundException ex) {
            // Handle case where income with given ID doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot delete: Income not found with ID: " + id);
                    
        } catch (Exception e) {
            // Handle any unexpected errors
            // TODO: Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the income. Please try again later.");
        }
    }
    
    // =========================================================================
    // FUTURE ENHANCEMENTS
    // =========================================================================
    
    // TODO: Add endpoint for filtering incomes by date range
    // @GetMapping("/filter")
    // public ResponseEntity<List<Income>> getIncomesByDateRange(
    //     @RequestParam LocalDate startDate,
    //     @RequestParam LocalDate endDate
    // )
    
    // TODO: Add endpoint for filtering incomes by category
    // @GetMapping("/category/{category}")
    // public ResponseEntity<List<Income>> getIncomesByCategory(
    //     @PathVariable String category
    // )
    
    // TODO: Add endpoint for searching incomes
    // @GetMapping("/search")
    // public ResponseEntity<List<Income>> searchIncomes(
    //     @RequestParam String query
    // )
    
    // TODO: Add pagination support
    // @GetMapping("/all")
    // public ResponseEntity<Page<Income>> getAllIncomes(Pageable pageable)
}
