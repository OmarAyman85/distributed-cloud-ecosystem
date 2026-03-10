package com.ayman.distributed.savvy.controller;

import com.ayman.distributed.savvy.services.advisor.FinancialAdvisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/advisor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Advisor", description = "AI Financial Advisor endpoints")
public class AdvisorController {

    private final FinancialAdvisorService advisorService;

    @GetMapping("/suggestions")
    @Operation(summary = "Get personalized AI financial advice")
    public ResponseEntity<?> getSuggestions() {
        try {
            String advice = advisorService.generateFinancialAdvice();
            return ResponseEntity.ok(Map.of("advice", advice));
        } catch (Exception e) {
            log.error("Failed to generate AI advice", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred while generating suggestions.", "details", e.getMessage()));
        }
    }
}
