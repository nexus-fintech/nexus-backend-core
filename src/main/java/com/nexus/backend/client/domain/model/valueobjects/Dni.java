package com.nexus.backend.client.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Value Object para el Documento Nacional de Identidad.
 * En el contexto de Microfinanzas, el DNI es un identificador único crítico.
 */
@Embeddable
public record Dni(
        @NotBlank @Size(min = 8, max = 12, message = "DNI must be between 8 and 12 characters")
        String number
) {
    public Dni {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("DNI number cannot be null or empty");
        }
    }
}