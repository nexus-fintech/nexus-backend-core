package com.nexus.backend.loan.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Output Resource (Response Body).
 * Exposes loan details, flattening Value Objects (Money, InterestRate)
 * into standard types (BigDecimal, Double) for JSON.
 */
public record LoanResource(
        Long id,
        Long clientId,
        BigDecimal requestedAmount,
        double annualInterestRate,
        int termInMonths,
        String status,
        LocalDate disbursementDate
) {
}