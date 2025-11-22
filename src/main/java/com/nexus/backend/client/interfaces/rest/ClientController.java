package com.nexus.backend.client.interfaces.rest;

import com.nexus.backend.client.domain.model.queries.GetClientByEmailQuery;
import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;
import com.nexus.backend.client.domain.services.ClientCommandService;
import com.nexus.backend.client.domain.services.ClientQueryService;
import com.nexus.backend.client.interfaces.rest.resources.ClientResource;
import com.nexus.backend.client.interfaces.rest.resources.CreateClientResource;
import com.nexus.backend.client.interfaces.rest.transform.ClientResourceFromEntityAssembler;
import com.nexus.backend.client.interfaces.rest.transform.CreateClientCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing client profiles.
 */
@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.GET,
        RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(value = "/api/v1/clients", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Clients", description = "Client Management Endpoints")
public class ClientController {

    private final ClientCommandService clientCommandService;
    private final ClientQueryService clientQueryService;

    /**
     * Constructor for ClientController.
     * Dependency Injection of Domain Services (Input Ports).
     *
     * @param clientCommandService the service to handle client commands
     * @param clientQueryService   the service to handle client queries
     */
    public ClientController(ClientCommandService clientCommandService, ClientQueryService clientQueryService) {
        this.clientCommandService = clientCommandService;
        this.clientQueryService = clientQueryService;
    }

    /**
     * Endpoint to create a new client.
     *
     * @param resource The client data to create (JSON).
     * @return The created client resource.
     */
    @PostMapping
    @Operation(summary = "Create a new Client", description = "Creates a new client in the system with the provided information.")
    public ResponseEntity<ClientResource> createClient(@RequestBody CreateClientResource resource) {
        // 1. Transform Resource (JSON) -> Command (Domain)
        var createClientCommand = CreateClientCommandFromResourceAssembler.toCommandFromResource(resource);

        // 2. Execute Command Service
        var client = clientCommandService.handle(createClientCommand);

        if (client.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 3. Transform Entity (Domain) -> Resource (JSON Response)
        var clientResource = ClientResourceFromEntityAssembler.toResourceFromEntity(client.get());

        return new ResponseEntity<>(clientResource, HttpStatus.CREATED);
    }

    /**
     * Endpoint to get a client by email.
     * Demonstrates the usage of the Query Service (CQRS).
     * Usage: GET /api/v1/clients?email=example@test.com
     *
     * @param email The email to search for.
     * @return The client resource if found.
     */
    @GetMapping
    @Operation(summary = "Get Client by Email", description = "Retrieves a client based on their email address.")
    public ResponseEntity<ClientResource> getClientByEmail(@RequestParam String email) {
        // 1. Create Query Object (Wrapping the String into the Value Object)
        var getClientByEmailQuery = new GetClientByEmailQuery(new EmailAddress(email));

        // 2. Execute Query Service
        var client = clientQueryService.handle(getClientByEmailQuery);

        if (client.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 3. Transform Entity -> Resource
        var clientResource = ClientResourceFromEntityAssembler.toResourceFromEntity(client.get());

        return ResponseEntity.ok(clientResource);
    }
}