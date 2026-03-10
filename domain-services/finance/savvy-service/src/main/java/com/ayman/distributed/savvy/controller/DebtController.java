package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.model.dto.DebtDTO;
import com.ayman.distributed.savvy.model.entity.Debt;
import com.ayman.distributed.savvy.services.debt.DebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debt")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Debt", description = "Debt management endpoints")
public class DebtController {

    private final DebtService debtService;

    @PostMapping
    @Operation(summary = "Create a new debt")
    public ResponseEntity<?> createDebt(@Valid @RequestBody DebtDTO debtDTO) {
        log.info("Creating new debt: {}", debtDTO.getTitle());
        Debt createdDebt = debtService.createDebt(debtDTO);
        if (createdDebt != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDebt);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create debt.");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all debts")
    public ResponseEntity<?> getAllDebts() {
        return ResponseEntity.ok(debtService.getAllDebts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get debt by ID")
    public ResponseEntity<?> getDebtById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(debtService.getDebtById(id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debt not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a debt")
    public ResponseEntity<?> updateDebt(@PathVariable Long id, @Valid @RequestBody DebtDTO debtDTO) {
        try {
            return ResponseEntity.ok(debtService.updateDebt(debtDTO, id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot update: Debt not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a debt")
    public ResponseEntity<?> deleteDebt(@PathVariable Long id) {
        try {
            debtService.deleteDebt(id);
            return ResponseEntity.ok("Debt deleted successfully");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot delete: Debt not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}
