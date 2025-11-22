package com.nexus.backend.client.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record StreetAddress(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String zipCode,
        @NotBlank String country
) {
    public StreetAddress {
        if (street == null || city == null || country == null) {
            throw new IllegalArgumentException("Incomplete address information");
        }
    }
}