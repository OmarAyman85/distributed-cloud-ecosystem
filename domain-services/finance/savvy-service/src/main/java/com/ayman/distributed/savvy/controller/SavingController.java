package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.model.dto.SavingDTO;
import com.ayman.distributed.savvy.model.entity.Saving;
import com.ayman.distributed.savvy.services.saving.SavingService;
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
@RequestMapping("/api/saving")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Saving", description = "Saving management endpoints")
public class SavingController {

    private final SavingService savingService;

    @PostMapping
    @Operation(summary = "Create a new saving goal")
    public ResponseEntity<?> createSaving(@Valid @RequestBody SavingDTO savingDTO) {
        log.info("Creating new saving: {}", savingDTO.getTitle());
        Saving createdSaving = savingService.createSaving(savingDTO);
        if (createdSaving != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSaving);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create saving.");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all savings")
    public ResponseEntity<?> getAllSavings() {
        return ResponseEntity.ok(savingService.getAllSavings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get saving by ID")
    public ResponseEntity<?> getSavingById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(savingService.getSavingById(id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Saving not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a saving")
    public ResponseEntity<?> updateSaving(@PathVariable Long id, @Valid @RequestBody SavingDTO savingDTO) {
        try {
            return ResponseEntity.ok(savingService.updateSaving(savingDTO, id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot update: Saving not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a saving")
    public ResponseEntity<?> deleteSaving(@PathVariable Long id) {
        try {
            savingService.deleteSaving(id);
            return ResponseEntity.ok("Saving deleted successfully");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot delete: Saving not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}
