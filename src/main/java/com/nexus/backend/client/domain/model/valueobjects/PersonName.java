package com.nexus.backend.client.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Value Object for a person's full name.
 */
@Embeddable
public record PersonName(
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName
) {
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
    }

    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}