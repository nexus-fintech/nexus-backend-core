package com.nexus.backend.loan.interfaces.rest.resources;

/**
 * Input Resource (Request Body) used to approve a loan.
 * Contains the client's updated financial data required for the
 * Risk Engine (Python) to perform the evaluation.
 */
public record ApproveLoanResource(
        int clientAge,
        double monthlyIncome,
        double monthlyDebt
) {
}