package com.nexus.backend.client.application.internal.commandservices;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.commands.CreateClientCommand;
import com.nexus.backend.client.domain.services.ClientCommandService;
import com.nexus.backend.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del Servicio de Comandos de Clientes.
 * Orquesta la lógica de escritura: Conversión y Persistencia.
 */
@Service
@RequiredArgsConstructor
public class ClientCommandServiceImpl implements ClientCommandService {

    private final ClientRepository clientRepository;

    /**
     * Maneja la creación de un nuevo cliente.
     * * @param command Datos primitivos del cliente.
     * @return El cliente creado y persistido.
     */
    @Override
    public Optional<Client> handle(CreateClientCommand command) {
        // 1. Convertimos el Command a la Entidad de Dominio.
        // Al hacer 'new Client(command)', se instancian los Value Objects (Email, DNI, etc.),
        // lo que dispara automáticamente las validaciones de formato.
        var client = new Client(command);

        // 2. Persistencia directa usando el repositorio de infraestructura.
        // La base de datos (MySQL) se encargará de validar unicidad (DNI/Email)
        // gracias a las restricciones @Column(unique = true) definidas en la entidad.
        var createdClient = clientRepository.save(client);

        return Optional.of(createdClient);
    }
}