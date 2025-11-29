package com.nexus.backend.loan.interfaces.rest;

import com.nexus.backend.loan.domain.model.queries.GetLoanByClientQuery;
import com.nexus.backend.loan.domain.model.queries.GetLoanByIdQuery;
import com.nexus.backend.loan.domain.services.LoanCommandService;
import com.nexus.backend.loan.domain.services.LoanQueryService;
import com.nexus.backend.loan.interfaces.rest.resources.ApproveLoanResource;
import com.nexus.backend.loan.interfaces.rest.resources.LoanResource;
import com.nexus.backend.loan.interfaces.rest.resources.RequestLoanResource;
import com.nexus.backend.loan.interfaces.rest.transform.ApproveLoanCommandFromResourceAssembler;
import com.nexus.backend.loan.interfaces.rest.transform.LoanResourceFromEntityAssembler;
import com.nexus.backend.loan.interfaces.rest.transform.RequestLoanCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * REST controller for managing loans.
 * Exposes endpoints for requesting, retrieving, and approving loans.
 */
@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.GET,
        RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(value = "/api/v1/loans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Loans", description = "Loan Management Endpoints")
public class LoanController {

    private final LoanCommandService loanCommandService;
    private final LoanQueryService loanQueryService;

    public LoanController(LoanCommandService loanCommandService, LoanQueryService loanQueryService) {
        this.loanCommandService = loanCommandService;
        this.loanQueryService = loanQueryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Request a new Loan", description = "Creates a loan application for a specific client.")
    public ResponseEntity<LoanResource> requestLoan(@RequestBody RequestLoanResource resource) {
        var requestLoanCommand = RequestLoanCommandFromResourceAssembler.toCommandFromResource(resource);
        var loan = loanCommandService.handle(requestLoanCommand);

        if (loan.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var loanResource = LoanResourceFromEntityAssembler.toResourceFromEntity(loan.get());
        return new ResponseEntity<>(loanResource, HttpStatus.CREATED);
    }

    /**
     * Endpoint to approve a loan based on risk evaluation.
     * Usage: POST /api/v1/loans/{loanId}/approve
     * Body: { "clientAge": 30, "monthlyIncome": 5000, "monthlyDebt": 1000 }
     */
    @PostMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a Loan", description = "Evaluates risk using external engine and approves loan if eligible.")
    public ResponseEntity<LoanResource> approveLoan(
            @PathVariable Long loanId,
            @RequestBody ApproveLoanResource resource) {

        // Transform Resource + ID -> Command
        var approveLoanCommand = ApproveLoanCommandFromResourceAssembler.toCommandFromResource(loanId, resource);

        // Execute Command Service (Triggers Risk Engine + Amortization)
        var loan = loanCommandService.handle(approveLoanCommand);

        if (loan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Return updated resource (Status could be ACTIVE or REJECTED depending on logic)
        var loanResource = LoanResourceFromEntityAssembler.toResourceFromEntity(loan.get());
        return ResponseEntity.ok(loanResource);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @Operation(summary = "Get Loans by Client", description = "Retrieves all loans associated with a specific client ID.")
    public ResponseEntity<List<LoanResource>> getLoansByClientId(@RequestParam Long clientId) {
        var getLoanByClientQuery = new GetLoanByClientQuery(clientId);
        var loans = loanQueryService.handle(getLoanByClientQuery);

        var loanResources = loans.stream()
                .map(LoanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(loanResources);
    }

    /**
     * Returns the FULL details including the payment schedule.
     */
    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @Operation(summary = "Get Loan Details", description = "Retrieves full loan details including amortization schedule.")
    public ResponseEntity<LoanResource> getLoanById(@PathVariable Long loanId) {
        var getLoanByIdQuery = new GetLoanByIdQuery(loanId);
        var loan = loanQueryService.handle(getLoanByIdQuery);

        if (loan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var loanResource = LoanResourceFromEntityAssembler.toResourceFromEntityWithSchedule(loan.get());

        return ResponseEntity.ok(loanResource);
    }
}