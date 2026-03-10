package com.ayman.distributed.savvy.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * =============================================================================
 * EXPENSE DTO - Data Transfer Object for Expense Transactions
 * =============================================================================
 * 
 * This DTO is used to transfer expense data between the client and server.
 * It includes validation annotations to ensure data integrity before
 * processing in the service layer.
 * 
 * Validation Rules:
 * - Title: Required, 3-100 characters
 * - Description: Optional, max 500 characters
 * - Category: Required, not blank
 * - Amount: Required, must be positive
 * - Date: Required, cannot be null
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@Data  // Lombok: Generates getters, setters, toString, equals, and hashCode
public class ExpenseDTO {

    /**
     * Expense ID (auto-generated, not required for creation).
     * Only used when updating or retrieving existing expenses.
     */
    private Long id;

    /**
     * Title of the expense.
     * 
     * Validation:
     * - Required (cannot be null or blank)
     * - Minimum length: 3 characters
     * - Maximum length: 100 characters
     * 
     * Examples: "Grocery Shopping", "Gas Station", "Netflix Subscription"
     */
    @NotBlank(message = "Title is required and cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    /**
     * Detailed description of the expense.
     * 
     * Validation:
     * - Optional (can be null or empty)
     * - Maximum length: 500 characters
     * 
     * Examples: "Weekly groceries from Walmart", "Filled up tank at Shell station"
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Category of the expense.
     * 
     * Validation:
     * - Required (cannot be null or blank)
     * 
     * Common categories: Food, Transport, Entertainment, Utilities, Healthcare, Shopping
     */
    @NotBlank(message = "Category is required and cannot be blank")
    private String category;

    /**
     * Date when the expense occurred.
     * 
     * Validation:
     * - Required (cannot be null)
     * - Should not be in the future (optional constraint)
     * 
     * Format: YYYY-MM-DD (e.g., 2026-01-28)
     */
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate date;

    /**
     * Amount of money spent in this expense.
     * 
     * Validation:
     * - Required (cannot be null)
     * - Must be positive (greater than 0)
     * 
     * Note: Stored as Integer (in cents/smallest currency unit)
     * Example: $150.00 = 15000 cents
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Integer amount;
}
