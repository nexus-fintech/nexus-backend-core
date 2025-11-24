package com.nexus.backend.loan.domain.model.queries;

/**
 * Query to retrieve all loans associated with a specific client.
 */
public record GetLoanByClientQuery(Long clientId) {
}