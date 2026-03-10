package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.dto.ExpenseDTO;
import com.ayman.distributed.savvy.model.entity.Expense;
import com.ayman.distributed.savvy.services.expense.ExpenseService;
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

import java.util.List;

/**
 * =============================================================================
 * EXPENSE CONTROLLER - Expense Management API
 * =============================================================================
 * 
 * This REST controller handles all CRUD (Create, Read, Update, Delete) operations
 * for expense transactions in the Savvy Expense Tracker application.
 * 
 * Expenses represent money spent by the user and are categorized for better
 * financial tracking and analysis.
 * 
 * Base URL: /api/expense
 * 
 * Endpoints:
 * - POST   /api/expense        - Create a new expense
 * - GET    /api/expense/all    - Get all expenses
 * - GET    /api/expense/{id}   - Get a specific expense by ID
 * - PUT    /api/expense/{id}   - Update an existing expense
 * - DELETE /api/expense/{id}   - Delete an expense
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@RestController  // Marks this class as a Spring MVC REST Controller (handles HTTP requests)
@RequestMapping("/api/expense")  // Defines the base URL path for all endpoints in this controller
@RequiredArgsConstructor  // Lombok annotation to generate a constructor with final fields (Dependency Injection)
@Slf4j  // Lombok: Generates SLF4J logger instance (log variable)
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {

    // =========================================================================
    // DEPENDENCIES
    // =========================================================================
    
    /**
     * Service layer for expense-related business logic.
     * Injected automatically by Spring via constructor injection (thanks to @RequiredArgsConstructor).
     */
    private final ExpenseService expenseService;

    // =========================================================================
    // CREATE OPERATIONS
    // =========================================================================
    
    /**
     * POST /api/expense
     * 
     * Creates a new expense transaction in the system.
     * 
     * This endpoint accepts expense details (title, description, amount, category, date)
     * and persists them to the database. The created expense is returned in the response.
     * 
     * @param expenseDTO Data Transfer Object containing expense details
     *                   - title: Name/title of the expense (e.g., "Grocery Shopping")
     *                   - description: Detailed description of the expense
     *                   - amount: Amount spent (in the user's currency)
     *                   - category: Category of expense (e.g., "Food", "Transport")
     *                   - date: Date when the expense occurred
     * 
     * @return ResponseEntity with HTTP 201 (Created) and the created Expense object if successful,
     *         or HTTP 400 (Bad Request) if the expense creation fails
     * 
     * @apiNote Validation is performed on the ExpenseDTO to ensure data integrity
     * 
     * @example
     * POST http://localhost:8080/api/expense
     * Body: {
     *   "title": "Grocery Shopping",
     *   "description": "Weekly groceries from supermarket",
     *   "amount": 150,
     *   "category": "Food",
     *   "date": "2026-01-28"
     * }
     */
    @PostMapping  // Maps this method to an HTTP POST request (URL: /api/expense)
    @Operation(
        summary = "Create a new expense",
        description = "Creates a new expense transaction with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Expense created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid expense data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> postExpense(
            @Valid @RequestBody @Parameter(description = "Expense details") ExpenseDTO expenseDTO) {
        
        log.info("Creating new expense: {}", expenseDTO.getTitle());
        
        // Call service layer to create the expense
        // Service layer handles business logic and database operations
        Expense createdExpense = expenseService.postExpense(expenseDTO);

        if (createdExpense != null) {
            log.info("Expense created successfully with ID: {}", createdExpense.getId());
            // If expense was successfully created, return HTTP 201 (Created) with the new expense data
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
        } else {
            log.warn("Failed to create expense: {}", expenseDTO.getTitle());
            // If expense creation failed, return HTTP 400 (Bad Request)
            // TODO: Return a more descriptive error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create expense. Please check your input data.");
        }
    }

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================
    
    /**
     * GET /api/expense/all
     * 
     * Retrieves all expense transactions from the database.
     * 
     * This endpoint returns a list of all expenses recorded by the user,
     * ordered by date (most recent first, typically).
     * 
     * @return ResponseEntity with HTTP 200 (OK) and a list of all Expense objects
     * 
     * @apiNote For large datasets, consider implementing pagination to improve performance
     * 
     * @example
     * GET http://localhost:8080/api/expense/all
     * Response: [
     *   { "id": 1, "title": "Grocery", "amount": 150, ... },
     *   { "id": 2, "title": "Gas", "amount": 50, ... }
     * ]
     */
    @GetMapping("/all")
    @Operation(
        summary = "Get all expenses",
        description = "Retrieves a list of all expense transactions"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllExpenses() {
        log.info("Fetching all expenses");
        
        // Delegate to service layer to fetch all expenses
        // TODO: Add pagination support for better performance with large datasets
        List<?> expenses = expenseService.getAllExpenses();
        
        log.debug("Retrieved {} expenses", expenses.size());
        return ResponseEntity.ok(expenses);
    }

    /**
     * GET /api/expense/{id}
     * 
     * Retrieves a specific expense transaction by its unique ID.
     * 
     * This endpoint is useful for viewing detailed information about a single expense,
     * or for populating an edit form with existing expense data.
     * 
     * @param id The unique identifier of the expense to retrieve
     * 
     * @return ResponseEntity with:
     *         - HTTP 200 (OK) and the Expense object if found
     *         - HTTP 404 (Not Found) if the expense doesn't exist
     *         - HTTP 500 (Internal Server Error) for unexpected errors
     * 
     * @throws EntityNotFoundException if no expense exists with the given ID
     * 
     * @example
     * GET http://localhost:8080/api/expense/1
     * Response: { "id": 1, "title": "Grocery", "amount": 150, ... }
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get expense by ID",
        description = "Retrieves a specific expense transaction by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense found and retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Expense not found with the given ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getExpenseById(
            @PathVariable @Parameter(description = "Expense ID") Long id) {
        try {
            // Attempt to retrieve the expense by ID
            Expense expense = expenseService.getExpenseById(id);
            return ResponseEntity.ok(expense);
            
        } catch (EntityNotFoundException ex) {
            // Handle case where expense with given ID doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Expense not found with ID: " + id);
                    
        } catch (Exception e) {
            // Handle any unexpected errors
            // TODO: Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the expense. Please try again later.");
        }
    }

    // =========================================================================
    // UPDATE OPERATIONS
    // =========================================================================
    
    /**
     * PUT /api/expense/{id}
     * 
     * Updates an existing expense transaction with new data.
     * 
     * This endpoint allows users to modify any field of an existing expense
     * (title, description, amount, category, date).
     * 
     * @param id The unique identifier of the expense to update
     * @param expenseDTO Data Transfer Object containing the updated expense details
     * 
     * @return ResponseEntity with:
     *         - HTTP 200 (OK) and the updated Expense object if successful
     *         - HTTP 404 (Not Found) if the expense doesn't exist
     *         - HTTP 500 (Internal Server Error) for unexpected errors
     * 
     * @throws EntityNotFoundException if no expense exists with the given ID
     * 
     * @example
     * PUT http://localhost:8080/api/expense/1
     * Body: {
     *   "title": "Grocery Shopping (Updated)",
     *   "description": "Monthly groceries",
     *   "amount": 200,
     *   "category": "Food",
     *   "date": "2026-01-28"
     * }
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an expense",
        description = "Updates an existing expense transaction with new details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
        @ApiResponse(responseCode = "404", description = "Expense not found with the given ID"),
        @ApiResponse(responseCode = "400", description = "Invalid expense data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateExpense(
            @PathVariable @Parameter(description = "Expense ID") Long id,
            @Valid @RequestBody @Parameter(description = "Updated expense details") ExpenseDTO expenseDTO) {
        try {
            // Attempt to update the expense
            Expense updatedExpense = expenseService.updateExpense(expenseDTO, id);
            return ResponseEntity.ok(updatedExpense);
            
        } catch (EntityNotFoundException ex) {
            // Handle case where expense with given ID doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot update: Expense not found with ID: " + id);
                    
        } catch (Exception e) {
            // Handle any unexpected errors
            // TODO: Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the expense. Please try again later.");
        }
    }

    // =========================================================================
    // DELETE OPERATIONS
    // =========================================================================
    
    /**
     * DELETE /api/expense/{id}
     * 
     * Deletes an expense transaction from the database.
     * 
     * This is a permanent operation and cannot be undone. The expense record
     * will be completely removed from the system.
     * 
     * @param id The unique identifier of the expense to delete
     * 
     * @return ResponseEntity with:
     *         - HTTP 200 (OK) if deletion was successful
     *         - HTTP 404 (Not Found) if the expense doesn't exist
     *         - HTTP 500 (Internal Server Error) for unexpected errors
     * 
     * @throws EntityNotFoundException if no expense exists with the given ID
     * 
     * @apiNote Consider implementing soft delete (marking as deleted instead of removing)
     *          to allow for data recovery and audit trails
     * 
     * @example
     * DELETE http://localhost:8080/api/expense/1
     * Response: HTTP 200 OK
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an expense",
        description = "Permanently deletes an expense transaction from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Expense not found with the given ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteExpense(
            @PathVariable @Parameter(description = "Expense ID") Long id) {
        try {
            // Attempt to delete the expense
            expenseService.deleteExpense(id);
            
            // Return success response
            // TODO: Consider returning a success message instead of null
            return ResponseEntity.ok("Expense deleted successfully");
            
        } catch (EntityNotFoundException ex) {
            // Handle case where expense with given ID doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot delete: Expense not found with ID: " + id);
                    
        } catch (Exception e) {
            // Handle any unexpected errors
            // TODO: Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the expense. Please try again later.");
        }
    }
    
    // =========================================================================
    // FUTURE ENHANCEMENTS
    // =========================================================================
    
    // TODO: Add endpoint for filtering expenses by date range
    // @GetMapping("/filter")
    // public ResponseEntity<List<Expense>> getExpensesByDateRange(
    //     @RequestParam LocalDate startDate,
    //     @RequestParam LocalDate endDate
    // )
    
    // TODO: Add endpoint for filtering expenses by category
    // @GetMapping("/category/{category}")
    // public ResponseEntity<List<Expense>> getExpensesByCategory(
    //     @PathVariable String category
    // )
    
    // TODO: Add endpoint for searching expenses
    // @GetMapping("/search")
    // public ResponseEntity<List<Expense>> searchExpenses(
    //     @RequestParam String query
    // )
    
    // TODO: Add pagination support
    // @GetMapping("/all")
    // public ResponseEntity<Page<Expense>> getAllExpenses(Pageable pageable)
}
