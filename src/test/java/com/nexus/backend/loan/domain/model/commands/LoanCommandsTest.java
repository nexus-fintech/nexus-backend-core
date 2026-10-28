package com.nexus.backend.loan.domain.model.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanCommandsTest {

    @Test
    void acceptsAValidLoanRequestAndItsEdges() {
        assertThatCode(() -> new RequestLoanCommand(1L, new BigDecimal("1000"), 12, 0.15)).doesNotThrowAnyException();
        assertThatCode(() -> new RequestLoanCommand(1L, new BigDecimal("0.01"), 1, 0.0)).doesNotThrowAnyException();
        assertThatCode(() -> new RequestLoanCommand(1L, new BigDecimal("1000"), 360, 1.0)).doesNotThrowAnyException();
    }

    static Stream<Arguments> invalidLoanRequests() {
        return Stream.of(
                Arguments.of(null, new BigDecimal("1000"), 12, 0.15),
                Arguments.of(1L, null, 12, 0.15),
                Arguments.of(1L, BigDecimal.ZERO, 12, 0.15),
                Arguments.of(1L, new BigDecimal("-500"), 12, 0.15),
                Arguments.of(1L, new BigDecimal("1000"), 0, 0.15),
                Arguments.of(1L, new BigDecimal("1000"), -3, 0.15),
                Arguments.of(1L, new BigDecimal("1000"), 12, -0.01),
                Arguments.of(1L, new BigDecimal("1000"), 12, 1.01)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLoanRequests")
    void rejectsAnInvalidLoanRequest(Long clientId, BigDecimal amount, int term, double rate) {
        assertThatThrownBy(() -> new RequestLoanCommand(clientId, amount, term, rate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsAValidApproval() {
        assertThatCode(() -> new ApproveLoanCommand(1L, 18, 0, 0)).doesNotThrowAnyException();
    }

    @Test
    void rejectsAnInvalidApproval() {
        assertThatThrownBy(() -> new ApproveLoanCommand(null, 30, 4000, 500)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ApproveLoanCommand(1L, 17, 4000, 500)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ApproveLoanCommand(1L, 30, -1, 500)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ApproveLoanCommand(1L, 30, 4000, -1)).isInstanceOf(IllegalArgumentException.class);
    }
}
