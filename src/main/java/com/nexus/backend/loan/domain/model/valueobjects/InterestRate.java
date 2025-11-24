package com.nexus.backend.loan.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value Object for the annual interest rate applied to the loan.
 * Represented as a percentage (e.g., 0.12 for 12%).
 */
@Embeddable
@Getter
@NoArgsConstructor
public class InterestRate {

    @DecimalMin(value = "0.00", inclusive = true)
    @DecimalMax(value = "1.00", inclusive = true) // Maximum 100%
    private BigDecimal rate;

    public InterestRate(BigDecimal rate) {
        if (rate == null) {
            throw new IllegalArgumentException("Rate cannot be null");
        }
        this.rate = rate;
    }

    public InterestRate(double rate) {
        this(BigDecimal.valueOf(rate));
    }
}