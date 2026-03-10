package com.ayman.distributed.delivery.features.bosta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryRequest {

    // Receiver Details
    @NotBlank(message = "Receiver first name is required")
    private String receiverFirstName;

    @NotBlank(message = "Receiver last name is required")
    private String receiverLastName;

    @NotBlank(message = "Receiver phone is required")
    private String receiverPhone;

    @NotBlank(message = "Receiver email is required")
    private String receiverEmail;

    // Address
    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Street address is required")
    private String streetAddress;
    
    private String buildingNumber;
    private String apartment;
    private String zone;

    // Shipment Details
    @NotNull(message = "COD amount is required")
    private Double codAmount; // Cash on Delivery amount

    private String description;
    
    // Pickup Address (Optional - can be defaulted in service)
    private String pickupCity;
    private String pickupStreetAddress;
}
