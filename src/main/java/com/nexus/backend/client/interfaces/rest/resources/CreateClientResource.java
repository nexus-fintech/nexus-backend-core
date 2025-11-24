package com.nexus.backend.client.interfaces.rest.resources;

/**
 * Input Resource (Request Body) for creating a client.
 * Represents the JSON sent by the Frontend.
 */
public record CreateClientResource(
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