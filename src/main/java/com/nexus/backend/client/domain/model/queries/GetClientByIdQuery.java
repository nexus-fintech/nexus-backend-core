package com.nexus.backend.client.domain.model.queries;

/**
 * Query to retrieve a client by its numeric ID.
 * Used for Master-Detail navigation.
 *
 * @param clientId The client's ID.
 */
public record GetClientByIdQuery(Long clientId) {
    public GetClientByIdQuery {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
    }
}
