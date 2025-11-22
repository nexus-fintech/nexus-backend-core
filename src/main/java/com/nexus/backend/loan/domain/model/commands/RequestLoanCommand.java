package com.nexus.backend.loan.domain.model.commands;

import com.nexus.backend.loan.domain.model.valueobjects.InterestRate;
import com.nexus.backend.loan.domain.model.valueobjects.Money;

import java.math.BigDecimal;

/**
 * Comando que inicia el proceso de solicitud de un microcrédito.
 * El backend se encargará de buscar el ClientId correspondiente.
 */
public record RequestLoanCommand(
        Long clientId, // ID del cliente del Bounded Context Client
        BigDecimal requestedAmount,
        int termInMonths,
        double annualInterestRate
) {
}