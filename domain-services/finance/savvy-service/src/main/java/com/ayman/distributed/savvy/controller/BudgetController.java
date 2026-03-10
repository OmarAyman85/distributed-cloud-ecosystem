package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.dto.BudgetDTO;
import com.ayman.distributed.savvy.model.entity.Budget;
import com.ayman.distributed.savvy.services.budget.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Budgets", description = "Budget management endpoints")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<Budget> createBudget(@RequestBody BudgetDTO budgetDTO) {
        log.info("Creating budget for category: {}", budgetDTO.getCategory());
        Budget budget = budgetService.createBudget(budgetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @GetMapping
    @Operation(summary = "Get all budgets with spending status")
    public ResponseEntity<List<BudgetDTO>> getAllBudgets() {
        List<BudgetDTO> budgets = budgetService.getAllBudgetsWithStatus();
        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody BudgetDTO budgetDTO) {
        Budget budget = budgetService.updateBudget(id, budgetDTO);
        return ResponseEntity.ok(budget);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<String> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok("Budget deleted successfully");
    }
}
