package com.nexus.backend.loan.infrastructure.persistence.jpa.repositories;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * Busca todos los préstamos que pertenecen a un cliente específico.
     * Spring Data JPA implementa esto automáticamente basándose en el campo 'clientId' de la entidad Loan.
     *
     * @param clientId El ID del cliente (Foreign Key).
     * @return Lista de préstamos asociados.
     */
    List<Loan> findByClientId(Long clientId);

}