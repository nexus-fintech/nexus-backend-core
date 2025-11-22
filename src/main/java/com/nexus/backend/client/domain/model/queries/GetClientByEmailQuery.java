package com.nexus.backend.client.domain.model.queries;

import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;

/**
 * Query para buscar un cliente por su email.
 * CQRS: Separa la intención de búsqueda de la lógica de negocio.
 */
public record GetClientByEmailQuery(EmailAddress emailAddress) {
}