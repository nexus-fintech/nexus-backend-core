package com.nexus.backend.client.application.internal.commandservices;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.commands.CreateClientCommand;
import com.nexus.backend.client.domain.model.valueobjects.Dni;
import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;
import com.nexus.backend.client.domain.services.ClientCommandService;
import com.nexus.backend.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the Client Command Service.
 * Orchestrates write logic: Conversion and Persistence.
 */
@Service
@RequiredArgsConstructor
public class ClientCommandServiceImpl implements ClientCommandService {

    private final ClientRepository clientRepository;

    /**
     * Handles the creation of a new client.
     * @param command Primitive client data.
     * @return The created and persisted client.
     */
    @Override
    public Optional<Client> handle(CreateClientCommand command) {

        var email = new EmailAddress(command.email());
        var dni = new Dni(command.dni());

        if (clientRepository.existsByEmail(email))
            throw new IllegalArgumentException("A client with this email already exists.");

        if (clientRepository.existsByDni(dni))
            throw new IllegalArgumentException("A client with this DNI already exists.");

        var client = new Client(command);
        var createdClient = clientRepository.save(client);

        return Optional.of(createdClient);
    }
}
