package com.ayman.distributed.savvy.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * =============================================================================
 * INCOME DTO - Data Transfer Object for Income Transactions
 * =============================================================================
 * 
 * This DTO is used to transfer income data between the client and server.
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
public class IncomeDTO {

    /**
     * Income ID (auto-generated, not required for creation).
     * Only used when updating or retrieving existing incomes.
     */
    private Long id;

    /**
     * Title of the income source.
     * 
     * Validation:
     * - Required (cannot be null or blank)
     * - Minimum length: 3 characters
     * - Maximum length: 100 characters
     * 
     * Examples: "Monthly Salary", "Freelance Project", "Stock Dividend"
     */
    @NotBlank(message = "Title is required and cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    /**
     * Detailed description of the income.
     * 
     * Validation:
     * - Optional (can be null or empty)
     * - Maximum length: 500 characters
     * 
     * Examples: "January 2026 salary from ABC Company", "Website development project"
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Category of the income.
     * 
     * Validation:
     * - Required (cannot be null or blank)
     * 
     * Common categories: Salary, Freelance, Business, Investment, Rental, Gifts, Refunds
     */
    @NotBlank(message = "Category is required and cannot be blank")
    private String category;

    /**
     * Amount of money received in this income transaction.
     * 
     * Validation:
     * - Required (cannot be null)
     * - Must be positive (greater than 0)
     * 
     * Note: Stored as Integer (in cents/smallest currency unit)
     * Example: $5000.00 = 500000 cents
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Integer amount;

    /**
     * Date when the income was received.
     * 
     * Validation:
     * - Required (cannot be null)
     * - Should not be in the future (optional constraint)
     * 
     * Format: YYYY-MM-DD (e.g., 2026-01-28)
     */
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Income date cannot be in the future")
    private LocalDate date;
}
