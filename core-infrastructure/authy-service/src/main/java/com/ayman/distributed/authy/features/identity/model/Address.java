package com.ayman.distributed.authy.features.identity.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Address {
    private String apartment;
    private String floor;
    private String street;
    private String area;
    private String city;
    private String country;
    private String postalCode;
}
