package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.interfaces.rest.resources.RequestLoanResource;

/**
 * Ensamblador para convertir de Recurso (JSON) a Comando (Dominio).
 */
public class RequestLoanCommandFromResourceAssembler {

    public static RequestLoanCommand toCommandFromResource(RequestLoanResource resource) {
        return new RequestLoanCommand(
                resource.clientId(),
                resource.amount(),
                resource.termInMonths(),
                resource.annualInterestRate()
        );
    }
}