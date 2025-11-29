package com.nexus.backend.loan.domain.model.queries;

/**
 * Query used to retrieve a specific loan by its ID.
 * It is used to view the full details, including the payment schedule.
 *
 * @param loanId The unique ID of the loan.
 */
public record GetLoanByIdQuery(Long loanId) {
    public GetLoanByIdQuery {
        if (loanId == null) {
            throw new IllegalArgumentException("Loan ID cannot be null");
        }
    }
}