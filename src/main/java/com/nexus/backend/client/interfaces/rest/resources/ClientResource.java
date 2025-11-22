package com.nexus.backend.client.interfaces.rest.resources;

/**
 * Recurso de Salida (Response Body).
 * Representa los datos del cliente que exponemos al exterior.
 * Aplanamos los Value Objects del dominio a tipos primitivos (String, Long).
 */
public record ClientResource(
        Long id,
        String firstName,
        String lastName,
        String email,
        String dni,
        String street,
        String city,
        String zipCode,
        String country
) {
}