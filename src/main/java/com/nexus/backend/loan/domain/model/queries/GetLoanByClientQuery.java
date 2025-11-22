package com.nexus.backend.loan.domain.model.queries;

/**
 * Consulta para obtener todos los préstamos asociados a un cliente específico.
 */
public record GetLoanByClientQuery(Long clientId) {
}