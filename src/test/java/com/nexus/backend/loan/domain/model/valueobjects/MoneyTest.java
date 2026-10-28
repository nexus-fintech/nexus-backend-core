package com.nexus.backend.loan.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void roundsToTwoDecimalsHalfUp() {
        assertThat(new Money(new BigDecimal("10.005")).getAmount()).isEqualByComparingTo("10.01");
        assertThat(new Money(new BigDecimal("10.004")).getAmount()).isEqualByComparingTo("10.00");
        assertThat(new Money(new BigDecimal("7")).getAmount().scale()).isEqualTo(2);
    }

    @Test
    void doubleAmountsDoNotKeepBinaryNoise() {
        assertThat(new Money(0.1 + 0.2).getAmount()).isEqualByComparingTo("0.30");
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new Money((BigDecimal) null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addsAndSubtracts() {
        var amount = new Money(new BigDecimal("100.10"));
        var fee = new Money(new BigDecimal("0.15"));
        assertThat(amount.add(fee).getAmount()).isEqualByComparingTo("100.25");
        assertThat(amount.subtract(fee).getAmount()).isEqualByComparingTo("99.95");
    }

    @Test
    void interestRateKeepsItsValueAndRejectsNull() {
        assertThat(new InterestRate(0.15).getRate()).isEqualByComparingTo("0.15");
        assertThatThrownBy(() -> new InterestRate((BigDecimal) null)).isInstanceOf(IllegalArgumentException.class);
    }
}
