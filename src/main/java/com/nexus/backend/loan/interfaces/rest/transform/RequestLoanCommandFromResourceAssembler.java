package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.interfaces.rest.resources.RequestLoanResource;

/**
 * Assembler to convert from Resource (JSON) to Command (Domain).
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