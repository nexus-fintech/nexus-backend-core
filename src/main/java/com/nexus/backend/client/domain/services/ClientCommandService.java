package com.nexus.backend.client.domain.services;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.commands.CreateClientCommand;

import java.util.Optional;

/**
 * Interface for the Client Command Service.
 * Defines the write operations (state mutations) available in the domain.
 * The implementation will reside in the Application layer.
 */
public interface ClientCommandService {

    /**
     * Handles the command to create a new client.
     * Orchestrates validation and persistence.
     *
     * @param command The data required to create the client.
     * @return The created client (Optional) if the operation was successful.
     */
    Optional<Client> handle(CreateClientCommand command);
}
