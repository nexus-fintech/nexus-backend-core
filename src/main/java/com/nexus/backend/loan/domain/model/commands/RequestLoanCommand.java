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
    public RequestLoanCommand {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (requestedAmount == null || requestedAmount.signum() <= 0) {
            throw new IllegalArgumentException("Requested amount must be greater than zero");
        }
        if (termInMonths < 1) {
            throw new IllegalArgumentException("Term must be at least one month");
        }
        if (annualInterestRate < 0 || annualInterestRate > 1) {
            throw new IllegalArgumentException("Annual interest rate must be between 0 and 1");
        }
    }
}