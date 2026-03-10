package com.ayman.distributed.authy.features.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for physical address information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address details")
public class AddressDTO {

    @Schema(description = "Apartment number or name", example = "12A")
    private String apartment;

    @Schema(description = "Floor number", example = "3rd Floor")
    private String floor;

    @Schema(description = "Street name", example = "Main Street")
    private String street;

    @Schema(description = "Area or neighborhood", example = "Downtown")
    private String area;

    @Schema(description = "City name", example = "Cairo")
    private String city;

    @Schema(description = "Country name", example = "Egypt")
    private String country;

    @Schema(description = "Postal or ZIP code", example = "12345")
    private String postalCode;
}
