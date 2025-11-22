package com.nexus.backend.client.interfaces.rest.transform;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.interfaces.rest.resources.ClientResource;

/**
 * Ensamblador para convertir de Entidad (Dominio) a Recurso (JSON de salida).
 * Extrae los valores primitivos de los Value Objects para exponerlos.
 */
public class ClientResourceFromEntityAssembler {

    public static ClientResource toResourceFromEntity(Client entity) {
        return new ClientResource(
                entity.getId(), // Heredado de AuditableAbstractAggregateRoot
                entity.getName().firstName(), // Extracción de VO PersonName
                entity.getName().lastName(),
                entity.getEmail().address(), // Extracción de VO EmailAddress
                entity.getDni().number(),    // Extracción de VO Dni
                entity.getAddress().street(), // Extracción de VO StreetAddress
                entity.getAddress().city(),
                entity.getAddress().zipCode(),
                entity.getAddress().country()
        );
    }
}