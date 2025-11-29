package com.nexus.backend.client.interfaces.rest.transform;

import com.nexus.backend.client.domain.model.commands.UpdateClientCommand;
import com.nexus.backend.client.interfaces.rest.resources.UpdateClientResource;

public class UpdateClientCommandFromResourceAssembler {
    public static UpdateClientCommand toCommandFromResource(Long id, UpdateClientResource resource) {
        return new UpdateClientCommand(
                id,
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
