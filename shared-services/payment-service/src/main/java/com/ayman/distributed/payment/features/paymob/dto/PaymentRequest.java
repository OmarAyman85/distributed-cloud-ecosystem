package com.ayman.distributed.payment.features.paymob.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency; // E.g., "EGP"

    // Customer Billing Data
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    // Optional Order Reference (for idempotency/tracking)
    private String orderId;
}
