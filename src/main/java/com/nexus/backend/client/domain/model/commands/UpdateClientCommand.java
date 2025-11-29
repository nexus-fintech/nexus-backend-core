package com.nexus.backend.client.domain.model.commands;

public record UpdateClientCommand(
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
    public UpdateClientCommand {
        if (id == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
    }
}