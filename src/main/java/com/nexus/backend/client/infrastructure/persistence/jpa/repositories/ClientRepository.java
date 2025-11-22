package com.nexus.backend.client.infrastructure.persistence.jpa.repositories;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.valueobjects.Dni;
import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA de Infraestructura.
 * Implementa métodos usando Value Objects para mayor seguridad de tipos,
 * alineado con las mejores prácticas de DDD.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Busca un cliente coincidiendo el objeto embebido Dni completo.
     * Spring Data compara los campos internos automáticamente.
     */
    Optional<Client> findByDni(Dni dni);

    /**
     * Busca un cliente coincidiendo el objeto embebido EmailAddress completo.
     */
    Optional<Client> findByEmail(EmailAddress email);

    /**
     * Verifica existencia por objeto Dni.
     */
    boolean existsByDni(Dni dni);

    /**
     * Verifica existencia por objeto EmailAddress.
     */
    boolean existsByEmail(EmailAddress email);
}