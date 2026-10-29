package com.nexus.backend.loan.interfaces.rest;

import com.nexus.backend.iam.interfaces.acl.IamContextFacade;
import com.nexus.backend.loan.application.internal.outboundservices.acl.ExternalClientService;
import com.nexus.backend.loan.domain.model.queries.GetLoanByIdQuery;
import com.nexus.backend.loan.domain.services.LoanQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Ownership checks for the loan endpoints, called from {@code @PreAuthorize}.
 *
 * <p>A client requests and reads only the loans of its own profile. An administrator is let
 * through by its role before this guard is asked.</p>
 */
@Component
public class LoanAccessGuard {

    private final LoanQueryService loanQueryService;
    private final ExternalClientService clientService;
    private final IamContextFacade iamContextFacade;

    public LoanAccessGuard(LoanQueryService loanQueryService, ExternalClientService clientService,
                           IamContextFacade iamContextFacade) {
        this.loanQueryService = loanQueryService;
        this.clientService = clientService;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Whether the caller owns the client profile with this id.
     *
     * @param authentication the caller
     * @param clientId the client id in the request
     * @return true only for the caller's own profile; false if it does not exist
     */
    public boolean ownsClient(Authentication authentication, Long clientId) {
        var callerId = iamContextFacade.fetchUserIdByUsername(authentication.getName());
        return clientService.fetchOwnerUserId(clientId)
                .map(ownerId -> ownerId.equals(callerId))
                .orElse(false);
    }

    /**
     * Whether the loan with this id belongs to the caller's client profile.
     *
     * @param authentication the caller
     * @param loanId the loan id in the request
     * @return true only for the caller's own loans; false if it does not exist
     */
    public boolean ownsLoan(Authentication authentication, Long loanId) {
        if (loanId == null) {
            return false;
        }
        return loanQueryService.handle(new GetLoanByIdQuery(loanId))
                .map(loan -> ownsClient(authentication, loan.getClientId()))
                .orElse(false);
    }
}
