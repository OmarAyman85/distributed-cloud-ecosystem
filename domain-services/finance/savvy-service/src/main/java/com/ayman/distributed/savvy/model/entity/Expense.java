package com.ayman.distributed.savvy.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * =============================================================================
 * EXPENSE ENTITY - Database Model for Expense Transactions
 * =============================================================================
 * 
 * This JPA entity represents an expense transaction in the Savvy Expense Tracker.
 * Each expense record tracks money spent by the user.
 * 
 * Database Table: expense
 * 
 * An expense includes:
 * - Unique identifier (ID)
 * - Title/name of the expense
 * - Detailed description
 * - Category for organization (e.g., Food, Transport, Entertainment)
 * - Amount spent
 * - Date when the expense occurred
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@Entity  // Marks this class as a JPA entity (maps to a database table)
@Data    // Lombok: Generates getters, setters, toString, equals, and hashCode methods
@Table(name = "expense")  // Specifies the database table name
public class Expense {

    /**
     * Unique identifier for the expense.
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
     * Title or name of the expense.
     * 
     * A short, descriptive name for the expense transaction.
     * 
     * Examples:
     * - "Grocery Shopping"
     * - "Gas Station"
     * - "Netflix Subscription"
     * - "Restaurant Dinner"
     */
    private String title;
    
    /**
     * Detailed description of the expense.
     * 
     * Optional field providing additional context about the expense.
     * Can include notes, receipts details, or any relevant information.
     * 
     * Examples:
     * - "Weekly groceries from Walmart"
     * - "Filled up tank at Shell station"
     * - "Monthly subscription payment"
     */
    private String description;
    
    /**
     * Category of the expense.
     * 
     * Used to group similar expenses for better financial analysis.
     * Helps users understand spending patterns by category.
     * 
     * Common categories:
     * - Food & Dining
     * - Transportation
     * - Entertainment
     * - Utilities
     * - Healthcare
     * - Shopping
     * - Education
     * - Housing
     * 
     * TODO: Consider converting this to an Enum for better type safety
     * TODO: Allow users to create custom categories
     */
    private String category;
    
    /**
     * Date when the expense occurred.
     * 
     * Stored as LocalDate (date only, no time component).
     * Used for filtering, sorting, and generating time-based reports.
     * 
     * Format: YYYY-MM-DD (e.g., 2026-01-28)
     */
    private LocalDate date;
    
    /**
     * Amount of money spent in this expense.
     * 
     * Stored as Integer (in cents/smallest currency unit to avoid floating-point issues).
     * For example: $150.00 would be stored as 15000 cents.
     * 
     * TODO: Consider using BigDecimal for better precision with large amounts
     * TODO: Add currency field for multi-currency support
     * 
     * Constraints:
     * - Must be positive (cannot have negative expenses)
     * - Should be validated in the DTO layer
     */
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
