package com.nexus.backend.client.domain.model.commands;

/**
 * Comando para crear un nuevo cliente.
 * Contiene solo los datos primitivos necesarios que vienen del exterior.
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