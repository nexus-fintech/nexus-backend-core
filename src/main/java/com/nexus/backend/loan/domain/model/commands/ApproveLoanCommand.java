package com.nexus.backend.loan.domain.model.commands;

/**
 * Command used to approve an existing loan.
 * Contains relevant and up-to-date financial information required
 * by the Risk Engine for the credit evaluation process.
 */
public record ApproveLoanCommand(
        Long loanId,
        int clientAge,
        double monthlyIncome,
        double monthlyDebt
) {

    /**
     * Compact constructor used for input validation.
     *
     * @throws IllegalArgumentException if:
     *         - loanId is null,
     *         - clientAge is below legal age (18),
     *         - monthly income or debt values are negative.
     */
    public ApproveLoanCommand {
        if (loanId == null) {
            throw new IllegalArgumentException("Loan ID cannot be null");
        }
        if (clientAge < 18) {
            throw new IllegalArgumentException("Client must be at least 18 years old");
        }
        if (monthlyIncome < 0 || monthlyDebt < 0) {
            throw new IllegalArgumentException("Financial amounts cannot be negative");
        }
    }
}
