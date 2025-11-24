package com.nexus.backend.loan.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Input Resource (Request Body) for requesting a loan.
 * Contains the primitive data sent by the client/advisor from the Frontend.
 */
public record RequestLoanResource(
        Long clientId,
        BigDecimal amount,
        int termInMonths,
        double annualInterestRate
) {
}