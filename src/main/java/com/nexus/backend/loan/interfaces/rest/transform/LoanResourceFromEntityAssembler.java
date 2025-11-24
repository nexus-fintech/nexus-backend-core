package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.interfaces.rest.resources.LoanResource;

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
                entity.getRequestedAmount().getAmount(),              // Extracting from Money VO
                entity.getAnnualInterestRate().getRate().doubleValue(), // Extracting from InterestRate VO
                entity.getTermInMonths(),
                entity.getStatus().name(),                             // Enum to String
                entity.getDisbursementDate()
        );
    }
}