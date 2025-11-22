package com.nexus.backend.loan.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Recurso de Entrada (Request Body) para solicitar un préstamo.
 * Contiene los datos primitivos que envía el cliente/asesor desde el Frontend.
 */
public record RequestLoanResource(
        Long clientId,
        BigDecimal amount,
        int termInMonths,
        double annualInterestRate
) {
}