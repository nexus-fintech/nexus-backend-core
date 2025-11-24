package com.nexus.backend.client.application.internal.queryservices;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.queries.GetClientByDniQuery;
import com.nexus.backend.client.domain.model.queries.GetClientByEmailQuery;
import com.nexus.backend.client.domain.services.ClientQueryService;
import com.nexus.backend.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the Client Query Service.
 * Optimized for read operations. Separates read responsibility from write responsibility (CQRS).
 */
@Service
@RequiredArgsConstructor
public class ClientQueryServiceImpl implements ClientQueryService {

    private final ClientRepository clientRepository;

    @Override
    public Optional<Client> handle(GetClientByEmailQuery query) {
        return clientRepository.findByEmail(query.emailAddress());
    }

    @Override
    public Optional<Client> handle(GetClientByDniQuery query) {
        return clientRepository.findByDni(query.dni());
    }
}
