package com.nexus.backend.client.interfaces.rest.resources;


/**
 * Output Resource (Response Body).
 * Represents the client data exposed to the outside world.
 * Domain Value Objects are flattened into primitive types (String, Long).
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