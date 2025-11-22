package com.nexus.backend.client.interfaces.rest.resources;

/**
 * Recurso de Entrada (Request Body) para crear un cliente.
 * Representa el JSON que envía el Frontend.
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