package com.ayman.distributed.savvy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * =============================================================================
 * INCOME ENTITY - Database Model for Income Transactions
 * =============================================================================
 * 
 * This JPA entity represents an income transaction in the Savvy Expense Tracker.
 * Each income record tracks money earned/received by the user.
 * 
 * Database Table: income
 * 
 * An income includes:
 * - Unique identifier (ID)
 * - Title/name of the income source
 * - Detailed description
 * - Category for organization (e.g., Salary, Freelance, Investment)
 * - Amount received
 * - Date when the income was received
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@Entity  // Marks this class as a JPA entity (maps to a database table)
@Data    // Lombok: Generates getters, setters, toString, equals, and hashCode methods
@Table(name = "income")  // Specifies the database table name
public class Income {
    
    /**
     * Unique identifier for the income.
     * 
     * This is the primary key in the database table.
     * Auto-generated using database auto-increment feature.
     * 
     * Example: 1, 2, 3, ...
     */
    @Id  // Marks this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Uses database auto-increment
    private long id;

    /**
     * Title or name of the income source.
     * 
     * A short, descriptive name for the income transaction.
     * 
     * Examples:
     * - "Monthly Salary"
     * - "Freelance Project Payment"
     * - "Stock Dividend"
     * - "Rental Income"
     * - "Bonus"
     */
    private String title;
    
    /**
     * Detailed description of the income.
     * 
     * Optional field providing additional context about the income source.
     * Can include notes, payment details, or any relevant information.
     * 
     * Examples:
     * - "January 2026 salary from ABC Company"
     * - "Website development project for XYZ Client"
     * - "Quarterly dividend from Apple stock"
     */
    private String description;
    
    /**
     * Category of the income.
     * 
     * Used to group similar income sources for better financial analysis.
     * Helps users understand income streams and diversification.
     * 
     * Common categories:
     * - Salary
     * - Freelance/Contract Work
     * - Business Income
     * - Investment Returns
     * - Rental Income
     * - Gifts
     * - Refunds
     * - Other
     * 
     * TODO: Consider converting this to an Enum for better type safety
     * TODO: Allow users to create custom categories
     */
    private String category;
    
    /**
     * Amount of money received in this income transaction.
     * 
     * Stored as Integer (in cents/smallest currency unit to avoid floating-point issues).
     * For example: $5000.00 would be stored as 500000 cents.
     * 
     * TODO: Consider using BigDecimal for better precision with large amounts
     * TODO: Add currency field for multi-currency support
     * 
     * Constraints:
     * - Must be positive (cannot have negative income)
     * - Should be validated in the DTO layer
     */
    private Integer amount;
    
    /**
     * Date when the income was received.
     * 
     * Stored as LocalDate (date only, no time component).
     * Used for filtering, sorting, and generating time-based reports.
     * 
     * Format: YYYY-MM-DD (e.g., 2026-01-28)
     */
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
