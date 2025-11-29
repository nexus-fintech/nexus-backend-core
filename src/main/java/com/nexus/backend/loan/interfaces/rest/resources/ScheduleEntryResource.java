package com.nexus.backend.loan.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Output Resource (Response Body) for a schedule installment.
 * Represents an individual item in the amortization table.
 */
public record ScheduleEntryResource(
        Long id,
        int installmentNumber,
        LocalDate dueDate,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal fee,
        BigDecimal totalAmount,
        boolean isPaid
) {
}