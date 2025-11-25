package com.nexus.backend.client.domain.services;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.queries.GetAllClientsQuery;
import com.nexus.backend.client.domain.model.queries.GetClientByDniQuery;
import com.nexus.backend.client.domain.model.queries.GetClientByEmailQuery;

import java.util.List;
import java.util.Optional;

/**
 * Interface for the Client Query Service.
 * Defines the read operations available in the domain.
 * Implements the CQRS pattern by separating read and write responsibilities.
 */
public interface ClientQueryService {

    /**
     * Handles the query for retrieving a client by their email.
     *
     * @param query Query object containing the search criteria.
     * @return The found client (Optional) or empty if none exists.
     */
    Optional<Client> handle(GetClientByEmailQuery query);

    /**
     * Handles the query for retrieving a client by their DNI.
     *
     * @param query Query object containing the search criteria.
     * @return The found client (Optional) or empty if none exists.
     */
    Optional<Client> handle(GetClientByDniQuery query);

    List<Client> handle(GetAllClientsQuery query);
}
