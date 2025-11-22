package com.nexus.backend.loan.domain.model.valueobjects;

/**
 * Value Object (Enum) para el estado del préstamo.
 * Define la máquina de estados dentro del agregado Loan.
 */
public enum LoanStatus {
    REQUESTED,  // Solicitado por el cliente (fase inicial)
    APPROVED,   // Aprobado por el motor de riesgo/administrador
    REJECTED,   // Rechazado (No apto o riesgo alto)
    ACTIVE,     // Desembolsado y en fase de pago
    DEFAULT,    // En mora (Incumplimiento de pagos)
    PAID        // Completamente pagado
}