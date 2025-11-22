package com.nexus.backend.loan.application.internal.commandservices;

import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.domain.services.LoanCommandService;
import com.nexus.backend.loan.infrastructure.persistence.jpa.repositories.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del Servicio de Comandos de Préstamos.
 * Orquesta la lógica de escritura: Creación de la solicitud de préstamo y persistencia.
 */
@Service
@RequiredArgsConstructor
public class LoanCommandServiceImpl implements LoanCommandService {

    private final LoanRepository loanRepository;

    /**
     * Maneja la solicitud de un nuevo préstamo.
     *
     * @param command Datos de la solicitud (monto, tasa, plazo).
     * @return El préstamo creado con estado REQUESTED.
     */
    @Override
    public Optional<Loan> handle(RequestLoanCommand command) {
        // 1. Instanciación del Agregado:
        // Convertimos el Command a la Entidad de Dominio.
        // Los Value Objects (Money, InterestRate) se crean dentro del constructor de Loan,
        // validando que los montos no sean negativos y las tasas sean correctas.
        var loan = new Loan(command);

        // 2. Persistencia:
        // Guardamos el préstamo en la base de datos.
        var createdLoan = loanRepository.save(loan);

        return Optional.of(createdLoan);
    }
}