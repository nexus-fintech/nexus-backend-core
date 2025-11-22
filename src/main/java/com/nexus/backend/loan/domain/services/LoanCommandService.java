package com.nexus.backend.loan.domain.services;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;

import java.util.Optional;

/**
 * Interface para el Servicio de Comandos de Préstamos.
 * Define las operaciones de escritura (solicitar, aprobar, pagar) disponibles.
 * La implementación residirá en la capa de Aplicación.
 */
public interface LoanCommandService {

    /**
     * Maneja el comando de solicitud de un nuevo préstamo.
     *
     * @param command Datos de la solicitud (monto, plazo, tasa).
     * @return El préstamo creado (Optional).
     */
    Optional<Loan> handle(RequestLoanCommand command);
}