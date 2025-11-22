package com.nexus.backend.loan.interfaces.rest;

import com.nexus.backend.loan.domain.model.queries.GetLoanByClientQuery;
import com.nexus.backend.loan.domain.services.LoanCommandService;
import com.nexus.backend.loan.domain.services.LoanQueryService;
import com.nexus.backend.loan.interfaces.rest.resources.LoanResource;
import com.nexus.backend.loan.interfaces.rest.resources.RequestLoanResource;
import com.nexus.backend.loan.interfaces.rest.transform.LoanResourceFromEntityAssembler;
import com.nexus.backend.loan.interfaces.rest.transform.RequestLoanCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing loans.
 */
@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.GET,
        RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(value = "/api/v1/loans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Loans", description = "Loan Management Endpoints")
public class LoanController {

    private final LoanCommandService loanCommandService;
    private final LoanQueryService loanQueryService;

    /**
     * Constructor for LoanController.
     * Dependency Injection of Domain Services (Input Ports).
     *
     * @param loanCommandService the service to handle loan commands
     * @param loanQueryService   the service to handle loan queries
     */
    public LoanController(LoanCommandService loanCommandService, LoanQueryService loanQueryService) {
        this.loanCommandService = loanCommandService;
        this.loanQueryService = loanQueryService;
    }

    /**
     * Endpoint to request a new loan.
     * Usage: POST /api/v1/loans
     *
     * @param resource The loan request data (JSON).
     * @return The created loan resource.
     */
    @PostMapping
    @Operation(summary = "Request a new Loan", description = "Creates a loan application for a specific client.")
    public ResponseEntity<LoanResource> requestLoan(@RequestBody RequestLoanResource resource) {
        // 1. Transform Resource (JSON) -> Command (Domain)
        var requestLoanCommand = RequestLoanCommandFromResourceAssembler.toCommandFromResource(resource);

        // 2. Execute Command Service
        var loan = loanCommandService.handle(requestLoanCommand);

        if (loan.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 3. Transform Entity (Domain) -> Resource (JSON Response)
        var loanResource = LoanResourceFromEntityAssembler.toResourceFromEntity(loan.get());

        return new ResponseEntity<>(loanResource, HttpStatus.CREATED);
    }

    /**
     * Endpoint to get loans by client ID.
     * Usage: GET /api/v1/loans?clientId=1
     *
     * @param clientId The client ID to filter loans.
     * @return A list of loan resources associated with the client.
     */
    @GetMapping
    @Operation(summary = "Get Loans by Client", description = "Retrieves all loans associated with a specific client ID.")
    public ResponseEntity<List<LoanResource>> getLoansByClientId(@RequestParam Long clientId) {
        // 1. Create Query Object
        var getLoanByClientQuery = new GetLoanByClientQuery(clientId);

        // 2. Execute Query Service
        var loans = loanQueryService.handle(getLoanByClientQuery);

        // 3. Transform List<Entity> -> List<Resource>
        // We stream the results and apply the assembler to each entity.
        var loanResources = loans.stream()
                .map(LoanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(loanResources);
    }
}