package com.nexus.backend.client.interfaces.rest.transform;

import com.nexus.backend.client.domain.model.commands.CreateClientCommand;
import com.nexus.backend.client.interfaces.rest.resources.CreateClientResource;

/**
 * Ensamblador para convertir de Recurso (JSON de entrada) a Comando (Dominio).
 */
public class CreateClientCommandFromResourceAssembler {

    public static CreateClientCommand toCommandFromResource(CreateClientResource resource) {
        return new CreateClientCommand(
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.dni(),
                resource.street(),
                resource.city(),
                resource.zipCode(),
                resource.country()
        );
    }
}