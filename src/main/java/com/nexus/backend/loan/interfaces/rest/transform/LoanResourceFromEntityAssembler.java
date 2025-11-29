package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.interfaces.rest.resources.LoanResource;

import java.util.Collections;

/**
 * Assembler to convert from Entity (Domain) to Resource (JSON).
 * Extracts primitive values from Value Objects (Money, InterestRate)
 * so they can be serialized to JSON.
 */
public class LoanResourceFromEntityAssembler {

    public static LoanResource toResourceFromEntity(Loan entity) {
        return new LoanResource(
                entity.getId(),
                entity.getClientId(),
                entity.getRequestedAmount().getAmount(),
                entity.getAnnualInterestRate().getRate().doubleValue(),
                entity.getTermInMonths(),
                entity.getStatus().name(),
                entity.getDisbursementDate(),
                Collections.emptyList() // Enviamos lista vacía para no cargar peso
        );
    }

    /**
     * Converts to a DETAILED Resource (with full schedule).
     * Used to view the full details (GET /loans/{id}).
     */
    public static LoanResource toResourceFromEntityWithSchedule(Loan entity) {
        var schedule = entity.getScheduleEntries().stream()
                .map(ScheduleEntryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new LoanResource(
                entity.getId(),
                entity.getClientId(),
                entity.getRequestedAmount().getAmount(),
                entity.getAnnualInterestRate().getRate().doubleValue(),
                entity.getTermInMonths(),
                entity.getStatus().name(),
                entity.getDisbursementDate(),
                schedule
        );
    }
}