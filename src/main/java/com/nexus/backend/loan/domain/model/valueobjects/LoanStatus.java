package com.nexus.backend.loan.domain.model.valueobjects;

/**
 * Value Object (Enum) for the loan status.
 * Defines the state machine within the Loan aggregate.
 */
public enum LoanStatus {
    REQUESTED,  // Requested by the client (initial phase)
    APPROVED,   // Approved by the risk engine/administrator
    REJECTED,   // Rejected (Not eligible or high risk)
    ACTIVE,     // Disbursed and in repayment phase
    DEFAULT,    // In default (Missed payments)
    PAID        // Fully paid
}