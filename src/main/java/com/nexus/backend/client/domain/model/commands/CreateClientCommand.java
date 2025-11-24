package com.nexus.backend.client.domain.model.commands;

/**
 * Command to create a new client.
 * Contains only the primitive data required, coming from the outside world.
 */
public record CreateClientCommand(
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