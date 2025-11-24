package com.nexus.backend.loan.infrastructure.persistence.jpa.repositories;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * Finds all loans belonging to a specific client.
     * Spring Data JPA implements this automatically based on the 'clientId' field in the Loan entity.
     *
     * @param clientId The client's ID (Foreign Key).
     * @return List of associated loans.
     */
    List<Loan> findByClientId(Long clientId);

}