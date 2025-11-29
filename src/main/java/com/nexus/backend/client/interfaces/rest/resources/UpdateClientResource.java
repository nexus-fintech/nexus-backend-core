package com.nexus.backend.client.interfaces.rest.resources;

/**
 * Input resource (Request Body) for updating a client.
 * Contains the editable fields of the profile.
 */
public record UpdateClientResource(
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