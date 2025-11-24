package com.nexus.backend.loan.domain.services;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.commands.ApproveLoanCommand;
import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;

import java.util.Optional;

/**
 * Loan Command Service Interface.
 *
 * Defines the write-side operations related to the Loan aggregate.
 * These include creating a new loan request and approving an existing one.
 * The concrete implementation is located in the Application layer.
 */
public interface LoanCommandService {

    /**
     * Handles the command for creating a new loan request.
     *
     * @param command the command containing loan request data
     *                (requested amount, interest rate, term, clientId).
     * @return an Optional containing the created Loan aggregate,
     *         or an empty Optional if creation failed.
     */
    Optional<Loan> handle(RequestLoanCommand command);

    /**
     * Handles the command for approving an existing loan.
     * This process includes validating the loan, evaluating credit risk,
     * generating the amortization schedule, and activating the loan.
     *
     * @param command the command containing the loanId and risk evaluation data.
     * @return an Optional with the updated and activated Loan aggregate,
     *         or an empty Optional if approval was not possible.
     */
    Optional<Loan> handle(ApproveLoanCommand command);
}