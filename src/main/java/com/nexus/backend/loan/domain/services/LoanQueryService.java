package com.nexus.backend.loan.domain.services;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.queries.GetLoanByClientQuery;
import com.nexus.backend.loan.domain.model.queries.GetLoanByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Interface for the Loan Query Service.
 * Defines read operations (CQRS).
 */
public interface LoanQueryService {

    /**
     * Handles the query to retrieve a client's loans.
     *
     * @param query Query object containing the client ID.
     * @return List of loans found.
     */
    List<Loan> handle(GetLoanByClientQuery query);
    Optional<Loan> handle(GetLoanByIdQuery query);
}