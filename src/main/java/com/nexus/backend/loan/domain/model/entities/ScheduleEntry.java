package com.nexus.backend.loan.domain.model.entities;

import com.nexus.backend.loan.domain.model.valueobjects.Money;
import com.nexus.backend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity representing an individual installment within the repayment schedule.
 * Lives and is managed by the Loan Aggregate.
 */
@Entity
@Getter
@NoArgsConstructor
public class ScheduleEntry extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int installmentNumber; // Installment number (1, 2, 3...)
    private LocalDate dueDate;     // Due date

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "principal_amount"))
    private Money principal;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest_amount"))
    private Money interest;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "fee_amount"))
    private Money fee;            // Fee or fixed charge (if applicable)

    private boolean isPaid;

    public ScheduleEntry(int installmentNumber, LocalDate dueDate, Money principal, Money interest, Money fee) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interest = interest;
        this.fee = fee;
        this.isPaid = false;
    }

    // Business method to mark the installment as paid
    public void markAsPaid() {
        this.isPaid = true;
    }

    // Helper method to get the total amount of the installment
    public Money getTotalAmount() {
        return principal.add(interest).add(fee);
    }
}