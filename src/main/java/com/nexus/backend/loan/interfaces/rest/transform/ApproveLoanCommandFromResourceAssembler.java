package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.commands.ApproveLoanCommand;
import com.nexus.backend.loan.interfaces.rest.resources.ApproveLoanResource;

/**
 * Assembler responsible for converting a Resource (JSON payload)
 * into a Domain Command.
 * It merges the path variable (loanId) with the request body values.
 */
public class ApproveLoanCommandFromResourceAssembler {

    public static ApproveLoanCommand toCommandFromResource(Long loanId, ApproveLoanResource resource) {
        return new ApproveLoanCommand(
                loanId,
                resource.clientAge(),
                resource.monthlyIncome(),
                resource.monthlyDebt()
        );
    }
}