package com.nexus.backend.client.application.internal.queryservices;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.queries.GetClientByEmailQuery;
import com.nexus.backend.client.domain.services.ClientQueryService;
import com.nexus.backend.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del Servicio de Consultas de Clientes.
 * Optimizado para lectura. Separa la responsabilidad de lectura de la escritura (CQRS).
 */
@Service
@RequiredArgsConstructor
public class ClientQueryServiceImpl implements ClientQueryService {

    private final ClientRepository clientRepository;

    @Override
    public Optional<Client> handle(GetClientByEmailQuery query) {
        // Delegamos la búsqueda al repositorio usando el Value Object directamente.
        // Esto aprovecha la definición del metodo findByEmail(EmailAddress email) en el repositorio.
        return clientRepository.findByEmail(query.emailAddress());
    }
}