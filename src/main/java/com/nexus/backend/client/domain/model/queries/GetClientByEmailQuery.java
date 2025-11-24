package com.nexus.backend.client.domain.model.queries;

import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;

/**
 * Query to search for a client by their email.
 * CQRS: Separates the search intent from the business logic.
 */
public record GetClientByEmailQuery(EmailAddress emailAddress) {
}
