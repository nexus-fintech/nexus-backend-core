package com.nexus.backend.client.infrastructure.persistence.jpa.repositories;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.valueobjects.Dni;
import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Infrastructure Repository.
 * Implements methods using Value Objects for stronger type safety,
 * aligned with DDD best practices.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Finds a client by matching the full embedded Dni value object.
     * Spring Data automatically compares the internal fields.
     */
    Optional<Client> findByDni(Dni dni);

    /**
     * Finds a client by matching the full embedded EmailAddress value object.
     */
    Optional<Client> findByEmail(EmailAddress email);

    /**
     * Checks existence by Dni value object.
     */
    boolean existsByDni(Dni dni);

    /**
     * Checks existence by EmailAddress value object.
     */
    boolean existsByEmail(EmailAddress email);
}
