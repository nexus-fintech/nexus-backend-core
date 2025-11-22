package com.nexus.backend.loan.domain.services;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.queries.GetLoanByClientQuery;

import java.util.List;

/**
 * Interface para el Servicio de Consultas de Préstamos.
 * Define las operaciones de lectura (CQRS).
 */
public interface LoanQueryService {

    /**
     * Maneja la consulta para obtener los préstamos de un cliente.
     *
     * @param query Objeto query con el ID del cliente.
     * @return Lista de préstamos encontrados.
     */
    List<Loan> handle(GetLoanByClientQuery query);
}