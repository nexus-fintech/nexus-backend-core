package com.nexus.backend.loan.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object to represent monetary amounts (principal, interest, installments).
 * It is essential to use BigDecimal to avoid precision errors in financial calculations.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class Money {

    private static final int DECIMAL_PLACES = 2;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    public Money(double amount) {
        this(BigDecimal.valueOf(amount));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }
}