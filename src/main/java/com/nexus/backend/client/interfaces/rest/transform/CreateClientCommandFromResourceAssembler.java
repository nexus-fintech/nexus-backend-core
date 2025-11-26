package com.nexus.backend.client.interfaces.rest.transform;

import com.nexus.backend.client.domain.model.commands.CreateClientCommand;
import com.nexus.backend.client.interfaces.rest.resources.CreateClientResource;

/**
 * Assembler to convert from Resource (JSON input) to Command (Domain).
 */
public class CreateClientCommandFromResourceAssembler {

    public static CreateClientCommand toCommandFromResource(CreateClientResource resource, Long userId) {
        return new CreateClientCommand(
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.dni(),
                resource.street(),
                resource.city(),
                resource.zipCode(),
                resource.country(),
                userId
        );
    }
}