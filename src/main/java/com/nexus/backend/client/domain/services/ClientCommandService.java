package com.nexus.backend.client.domain.services;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.commands.CreateClientCommand;

import java.util.Optional;

/**
 * Interface para el Servicio de Comandos de Clientes.
 * Define las operaciones de escritura (mutación de estado) disponibles en el dominio.
 * La implementación residirá en la capa de Aplicación.
 */
public interface ClientCommandService {

    /**
     * Maneja el comando de creación de un nuevo cliente.
     * Orquesta la validación y persistencia.
     *
     * @param command Datos necesarios para crear el cliente.
     * @return El cliente creado (Optional) si la operación fue exitosa.
     */
    Optional<Client> handle(CreateClientCommand command);
}