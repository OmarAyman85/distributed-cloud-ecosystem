package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.dto.GraphDTO;
import com.ayman.distributed.savvy.services.stats.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * =============================================================================
 * STATS CONTROLLER - Financial Statistics and Analytics
 * =============================================================================
 * 
 * This REST controller handles all endpoints related to financial statistics,
 * analytics, and data visualization for the Savvy Expense Tracker application.
 * 
 * It provides aggregated data for:
 * - Overall financial statistics (total income, total expenses, balance)
 * - Chart data for visualizing income vs expenses over time
 * - Monthly/yearly financial summaries
 * 
 * Base URL: /api/stats
 * 
 * @author Omar Ayman
 * @version 1.0
 * @since 2026-01-28
 * =============================================================================
 */
@RestController  // Marks this class as a REST controller that handles HTTP requests
@RequestMapping("/api/stats")  // Base URL path for all endpoints in this controller
@RequiredArgsConstructor  // Lombok: Generates constructor for final fields (dependency injection)
@Slf4j  // Lombok: Generates SLF4J logger instance (log variable)
@Tag(name = "Statistics", description = "Financial statistics and analytics endpoints")
public class StatsController {
    
    // =========================================================================
    // DEPENDENCIES
    // =========================================================================
    
    /**
     * Service layer for business logic related to financial statistics.
     * Injected automatically by Spring via constructor injection.
     */
    private final StatsService statsService;

    // =========================================================================
    // API ENDPOINTS
    // =========================================================================
    
    /**
     * GET /api/stats/chart
     * 
     * Retrieves chart data for visualizing income vs expenses over time.
     * This endpoint provides data formatted for chart libraries (e.g., ngx-charts)
     * to display financial trends graphically.
     * 
     * The returned data typically includes:
     * - Time series data (monthly/yearly)
     * - Income amounts per period
     * - Expense amounts per period
     * - Net balance per period
     * 
     * @return ResponseEntity containing GraphDTO with chart-ready data
     * 
     * @apiNote This endpoint is typically called when loading the dashboard
     *          to display visual representations of financial data.
     * 
     * @example
     * GET http://localhost:8080/api/stats/chart
     * Response: {
     *   "incomeData": [...],
     *   "expenseData": [...],
     *   "labels": ["Jan", "Feb", "Mar", ...]
     * }
     */
    @GetMapping("/chart")
    @Operation(
        summary = "Get chart data",
        description = "Retrieves financial data formatted for chart visualization"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chart data retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GraphDTO> getChartData() {
        log.info("Fetching chart data for financial visualization");
        
        // Delegate to service layer for business logic
        GraphDTO chartData = statsService.getChartData();
        
        log.debug("Chart data retrieved successfully");
        // Return HTTP 200 OK with the chart data
        return ResponseEntity.ok(chartData);
    }

    /**
     * GET /api/stats
     * 
     * Retrieves overall financial statistics and summary information.
     * This endpoint provides a comprehensive overview of the user's financial status.
     * 
     * The returned statistics typically include:
     * - Total income (sum of all income transactions)
     * - Total expenses (sum of all expense transactions)
     * - Current balance (income - expenses)
     * - Latest transactions
     * - Category-wise breakdown
     * - Monthly comparisons
     * 
     * @return ResponseEntity containing StatsDTO with financial statistics
     * 
     * @apiNote This is usually the first endpoint called when loading the dashboard
     *          to display key financial metrics and KPIs.
     * 
     * @example
     * GET http://localhost:8080/api/stats
     * Response: {
     *   "totalIncome": 50000,
     *   "totalExpense": 30000,
     *   "balance": 20000,
     *   "latestTransactions": [...]
     * }
     */
    @GetMapping
    @Operation(
        summary = "Get financial statistics",
        description = "Retrieves comprehensive financial statistics including income, expenses, and balance"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getStats() {
        log.info("Fetching financial statistics summary");
        
        // Delegate to service layer for business logic
        // Using wildcard <?> to allow flexibility in response type
        // TODO: Consider using specific return type (StatsDTO) instead of wildcard
        Object stats = statsService.getStats();
        
        log.debug("Financial statistics retrieved successfully");
        return ResponseEntity.ok(stats);
    }
    
    // =========================================================================
    // FUTURE ENHANCEMENTS
    // =========================================================================
    
    // TODO: Add endpoint for date-range filtered statistics
    // @GetMapping("/range")
    // public ResponseEntity<StatsDTO> getStatsByDateRange(
    //     @RequestParam LocalDate startDate,
    //     @RequestParam LocalDate endDate
    // )
    
    // TODO: Add endpoint for category-wise statistics
    // @GetMapping("/by-category")
    // public ResponseEntity<List<CategoryStatsDTO>> getStatsByCategory()
    
    // TODO: Add endpoint for monthly comparison
    // @GetMapping("/monthly-comparison")
    // public ResponseEntity<MonthlyComparisonDTO> getMonthlyComparison()
}
