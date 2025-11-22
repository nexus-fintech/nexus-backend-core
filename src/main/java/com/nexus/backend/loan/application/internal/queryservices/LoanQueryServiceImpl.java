package com.nexus.backend.loan.application.internal.queryservices;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.queries.GetLoanByClientQuery;
import com.nexus.backend.loan.domain.services.LoanQueryService;
import com.nexus.backend.loan.infrastructure.persistence.jpa.repositories.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del Servicio de Consultas de Préstamos.
 * Orquesta la lógica de lectura: Obtención de historial crediticio.
 */
@Service
@RequiredArgsConstructor
public class LoanQueryServiceImpl implements LoanQueryService {

    private final LoanRepository loanRepository;

    /**
     * Obtiene todos los préstamos asociados a un cliente específico.
     *
     * @param query Objeto query que contiene el ID del cliente.
     * @return Lista de préstamos encontrados.
     */
    @Override
    public List<Loan> handle(GetLoanByClientQuery query) {
        // Delegamos la búsqueda al repositorio usando el ID provisto en el query.
        return loanRepository.findByClientId(query.clientId());
    }
}