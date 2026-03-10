package com.ayman.distributed.payment.features.paymob.service;

import com.ayman.distributed.payment.features.paymob.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymobService {

    @Value("${paymob.api.base-url}")
    private String baseUrl;

    @Value("${paymob.api.intention-endpoint}")
    private String intentionEndpoint;

    @Value("${paymob.secret-key}")
    private String secretKey;

    @Value("${paymob.public-key}")
    private String publicKey;

    private final RestClient.Builder restClientBuilder;

    public String initiatePayment(PaymentRequest request) {
        log.info("Initiating payment for amount: {} {}", request.getAmount(), request.getCurrency());

        // Construct Paymob Payload
        Map<String, Object> payload = new HashMap<>();
        // Amount must be in cents/piasters
        payload.put("amount", (int) (request.getAmount() * 100));
        payload.put("currency", request.getCurrency());

        // Payment Methods (Integration IDs from reference: 5459284 Card, 4431954 Wallet)
        // Ideally, these should be configurable or dynamic based on user selection
        List<Integer> paymentMethods = new ArrayList<>();
        paymentMethods.add(5459284); // Card
        paymentMethods.add(4431954); // Wallet
        payload.put("payment_methods", paymentMethods);

        // Billing Data
        Map<String, String> billingData = new HashMap<>();
        billingData.put("first_name", request.getFirstName());
        billingData.put("last_name", request.getLastName());
        billingData.put("email", request.getEmail());
        billingData.put("phone_number", request.getPhoneNumber());
        
        // Mandatory fields that can be defaulted if not provided
        billingData.put("apartment", "NA");
        billingData.put("floor", "NA");
        billingData.put("street", "NA");
        billingData.put("building", "NA");
        billingData.put("shipping_method", "PKG");
        billingData.put("postal_code", "NA");
        billingData.put("city", "Cairo");
        billingData.put("country", "EG");
        billingData.put("state", "Cairo");

        payload.put("billing_data", billingData);

        // Order Reference
        if (request.getOrderId() != null) {
            payload.put("special_reference", request.getOrderId());
        }

        try {
            RestClient restClient = restClientBuilder.build();
            Map response = restClient.post()
                    .uri(baseUrl + intentionEndpoint)
                    .header("Authorization", "Token " + secretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("client_secret")) {
                String clientSecret = (String) response.get("client_secret");
                // Construct Unified Checkout URL
                return "https://accept.paymob.com/unifiedcheckout/?publicKey=" + publicKey + "&clientSecret=" + clientSecret;
            } else {
                throw new RuntimeException("Failed to retrieve client_secret from Paymob");
            }

        } catch (Exception e) {
            log.error("Error creating Paymob Intention", e);
            throw new RuntimeException("Payment initiation failed", e);
        }
    }

    public boolean verifyCallback(Map<String, Object> payload, String hmac) {
        // TODO: Implement HMAC Verification logic here
        // For now, we log the payload
        log.info("Received Callback Payload: {}", payload);
        log.info("Received HMAC: {}", hmac);
        
        // Simple success check for now (mirrors basic PHP logic)
        Object success = payload.get("success");
        return Boolean.TRUE.equals(success) || "true".equals(success);
    }
}
