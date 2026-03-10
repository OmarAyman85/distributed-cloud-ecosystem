package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.model.dto.InvestmentDTO;
import com.ayman.distributed.savvy.model.entity.Investment;
import com.ayman.distributed.savvy.services.investment.InvestmentService;
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
@RequestMapping("/api/investment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Investment", description = "Investment management endpoints")
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    @Operation(summary = "Create a new investment")
    public ResponseEntity<?> createInvestment(@Valid @RequestBody InvestmentDTO investmentDTO) {
        log.info("Creating new investment: {}", investmentDTO.getTitle());
        Investment createdInvestment = investmentService.createInvestment(investmentDTO);
        if (createdInvestment != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvestment);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create investment.");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all investments")
    public ResponseEntity<?> getAllInvestments() {
        return ResponseEntity.ok(investmentService.getAllInvestments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get investment by ID")
    public ResponseEntity<?> getInvestmentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(investmentService.getInvestmentById(id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Investment not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an investment")
    public ResponseEntity<?> updateInvestment(@PathVariable Long id, @Valid @RequestBody InvestmentDTO investmentDTO) {
        try {
            return ResponseEntity.ok(investmentService.updateInvestment(investmentDTO, id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot update: Investment not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an investment")
    public ResponseEntity<?> deleteInvestment(@PathVariable Long id) {
        try {
            investmentService.deleteInvestment(id);
            return ResponseEntity.ok("Investment deleted successfully");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot delete: Investment not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}
