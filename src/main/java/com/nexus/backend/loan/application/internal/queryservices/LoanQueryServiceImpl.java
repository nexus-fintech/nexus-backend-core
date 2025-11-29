package com.nexus.backend.loan.application.internal.queryservices;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.queries.GetLoanByClientQuery;
import com.nexus.backend.loan.domain.model.queries.GetLoanByIdQuery;
import com.nexus.backend.loan.domain.services.LoanQueryService;
import com.nexus.backend.loan.infrastructure.persistence.jpa.repositories.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Loan Query Service.
 * Orchestrates read logic: Retrieval of credit history.
 */
@Service
@RequiredArgsConstructor
public class LoanQueryServiceImpl implements LoanQueryService {

    private final LoanRepository loanRepository;

    /**
     * Retrieves all loans associated with a specific client.
     *
     * @param query Query object containing the client ID.
     * @return List of loans found.
     */
    @Override
    public List<Loan> handle(GetLoanByClientQuery query) {
        // Delegate the search to the repository using the ID provided in the query.
        return loanRepository.findByClientId(query.clientId());
    }

    /**
     * Handles search by ID.
     * Uses the standard JpaRepository method.
     */
    @Override
    public Optional<Loan> handle(GetLoanByIdQuery query) {
        return loanRepository.findById(query.loanId());
    }


}