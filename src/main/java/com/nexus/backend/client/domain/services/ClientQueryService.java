package com.nexus.backend.client.domain.services;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.queries.GetClientByDniQuery;
import com.nexus.backend.client.domain.model.queries.GetClientByEmailQuery;

import java.util.Optional;

/**
 * Interface para el Servicio de Consultas de Clientes.
 * Define las operaciones de lectura disponibles en el dominio.
 * Implementa el patrón CQRS separando la lectura de la escritura.
 */
public interface ClientQueryService {

    /**
     * Maneja la consulta para obtener un cliente por su email.
     *
     * @param query Objeto query con el criterio de búsqueda.
     * @return El cliente encontrado (Optional) o vacío si no existe.
     */
    Optional<Client> handle(GetClientByEmailQuery query);

    Optional<Client> handle(GetClientByDniQuery query);
}