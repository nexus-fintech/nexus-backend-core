package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.interfaces.rest.resources.LoanResource;

/**
 * Ensamblador para convertir de Entidad (Dominio) a Recurso (JSON).
 * Extrae los valores primitivos de los Value Objects (Money, InterestRate)
 * para que puedan ser serializados a JSON.
 */
public class LoanResourceFromEntityAssembler {

    public static LoanResource toResourceFromEntity(Loan entity) {
        return new LoanResource(
                entity.getId(),
                entity.getClientId(),
                entity.getRequestedAmount().getAmount(),       // Extracción de VO Money
                entity.getAnnualInterestRate().getRate().doubleValue(), // Extracción de VO InterestRate
                entity.getTermInMonths(),
                entity.getStatus().name(),                     // Enum a String
                entity.getDisbursementDate()
        );
    }
}