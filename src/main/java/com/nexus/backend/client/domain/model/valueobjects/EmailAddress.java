package com.nexus.backend.client.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

/**
 * Value Object representing an email address.
 * Ensures the integrity of the email format within the domain.
 */
@Embeddable
public record EmailAddress(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String address
) {
    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or empty");
        }
    }
}