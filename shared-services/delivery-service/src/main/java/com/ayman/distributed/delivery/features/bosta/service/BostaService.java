package com.ayman.distributed.delivery.features.bosta.service;

import com.ayman.distributed.delivery.features.bosta.dto.DeliveryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BostaService {

    @Value("${bosta.api.base-url}")
    private String baseUrl;

    @Value("${bosta.api-key}")
    private String apiKey;

    private final RestClient.Builder restClientBuilder;

    public Map<String, Object> createShipment(DeliveryRequest request) {
        log.info("Creating shipment for receiver: {} {}", request.getReceiverFirstName(), request.getReceiverLastName());

        // Construct Bosta Payload
        Map<String, Object> payload = new HashMap<>();
        
        // Receiver
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("firstName", request.getReceiverFirstName());
        receiver.put("lastName", request.getReceiverLastName());
        receiver.put("phone", request.getReceiverPhone());
        receiver.put("email", request.getReceiverEmail());
        payload.put("receiver", receiver);

        // DropOff Address (Receiver Address)
        Map<String, Object> dropOffAddress = new HashMap<>();
        dropOffAddress.put("city", request.getCity()); // Bosta expects City Code or Name
        dropOffAddress.put("firstLine", request.getStreetAddress());
        if (request.getBuildingNumber() != null) dropOffAddress.put("buildingNumber", request.getBuildingNumber());
        if (request.getApartment() != null) dropOffAddress.put("apartment", request.getApartment());
        if (request.getZone() != null) dropOffAddress.put("zone", request.getZone());
        payload.put("dropOffAddress", dropOffAddress);

        // Shipment Details
        payload.put("cod", request.getCodAmount());
        payload.put("type", 10); // 10 = Standard Delivery (Example type)
        if (request.getDescription() != null) payload.put("description", request.getDescription());

        // Pickup Address (Optional override, otherwise uses default on account)
        if (request.getPickupCity() != null && request.getPickupStreetAddress() != null) {
             Map<String, Object> pickupAddress = new HashMap<>();
             pickupAddress.put("city", request.getPickupCity());
             pickupAddress.put("firstLine", request.getPickupStreetAddress());
             payload.put("pickupAddress", pickupAddress);
        }

        try {
            RestClient restClient = restClientBuilder.build();
            return restClient.post()
                    .uri(baseUrl + "/api/v0/deliveries")
                    .header("Authorization", apiKey) // Bosta uses API Key in Authorization header usually
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

        } catch (Exception e) {
            log.error("Error creating Bosta shipment", e);
            throw new RuntimeException("Shipment creation failed: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> trackShipment(String trackingNumber) {
        log.info("Tracking shipment: {}", trackingNumber);

        try {
            RestClient restClient = restClientBuilder.build();
            return restClient.get()
                    .uri(baseUrl + "/api/v0/deliveries/" + trackingNumber)
                    .header("Authorization", apiKey)
                    .retrieve()
                    .body(Map.class);

        } catch (Exception e) {
            log.error("Error tracking Bosta shipment", e);
            throw new RuntimeException("Tracking failed: " + e.getMessage(), e);
        }
    }
}
