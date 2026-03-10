package com.ayman.distributed.delivery.features.bosta.controller;

import com.ayman.distributed.delivery.features.bosta.dto.DeliveryRequest;
import com.ayman.distributed.delivery.features.bosta.service.BostaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery API", description = "Endpoints for managing shipments via Bosta")
public class DeliveryController {

    private final BostaService bostaService;

    @Operation(summary = "Create Shipment", description = "Creates a new shipment in Bosta")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createShipment(@RequestBody @Valid DeliveryRequest request) {
        Map<String, Object> response = bostaService.createShipment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Track Shipment", description = "Retrieves shipment status from Bosta")
    @GetMapping("/{trackingNumber}")
    public ResponseEntity<Map<String, Object>> trackShipment(@PathVariable String trackingNumber) {
        Map<String, Object> response = bostaService.trackShipment(trackingNumber);
        return ResponseEntity.ok(response);
    }
}
