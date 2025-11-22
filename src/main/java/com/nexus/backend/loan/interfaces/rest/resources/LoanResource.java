package com.nexus.backend.loan.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Recurso de Salida (Response Body).
 * Expone los detalles del préstamo, aplanando los Value Objects (Money, InterestRate)
 * a tipos estándar (BigDecimal, Double) para el JSON.
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