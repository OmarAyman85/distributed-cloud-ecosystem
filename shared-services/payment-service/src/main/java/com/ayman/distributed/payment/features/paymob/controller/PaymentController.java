package com.ayman.distributed.payment.features.paymob.controller;

import com.ayman.distributed.payment.features.paymob.dto.PaymentRequest;
import com.ayman.distributed.payment.features.paymob.service.PaymobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Endpoints for initiating and handling payments via Paymob")
public class PaymentController {

    private final PaymobService paymobService;

    @Operation(summary = "Initiate Payment", description = "Creates a Paymob Intention and returns the redirect URL")
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment(@RequestBody @Valid PaymentRequest request) {
        String redirectUrl = paymobService.initiatePayment(request);
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    @Operation(summary = "Payment Callback", description = "Webhook handler for payment status updates")
    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestBody Map<String, Object> payload,
            @RequestParam(required = false) String hmac) {
        
        boolean processed = paymobService.verifyCallback(payload, hmac);
        
        if (processed) {
            return ResponseEntity.ok().build();
        } else {
            // Acknowledge receipt even on failure/invalid HMAC to prevent retries (consistent with reference implementation)
            return ResponseEntity.ok().build(); 
        }
    }
}
