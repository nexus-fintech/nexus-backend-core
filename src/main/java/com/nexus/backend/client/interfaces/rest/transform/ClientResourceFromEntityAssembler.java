package com.nexus.backend.client.interfaces.rest.transform;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.interfaces.rest.resources.ClientResource;

/**
 * Assembler to convert from Entity (Domain) to Resource (JSON output).
 * Extracts primitive values from Value Objects to expose them externally.
 */
public class ClientResourceFromEntityAssembler {

    public static ClientResource toResourceFromEntity(Client entity) {
        return new ClientResource(
                entity.getId(),                       // Inherited from AuditableAbstractAggregateRoot
                entity.getName().firstName(),         // Extracting from PersonName VO
                entity.getName().lastName(),
                entity.getEmail().address(),          // Extracting from EmailAddress VO
                entity.getDni().number(),             // Extracting from Dni VO
                entity.getAddress().street(),         // Extracting from StreetAddress VO
                entity.getAddress().city(),
                entity.getAddress().zipCode(),
                entity.getAddress().country()
        );
    }
}