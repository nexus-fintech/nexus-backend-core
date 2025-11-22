package com.nexus.backend.loan.domain.model.entities;

import com.nexus.backend.loan.domain.model.valueobjects.Money;
import com.nexus.backend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad que representa una cuota individual dentro del cronograma de pagos.
 * Vive y es gestionada por el Agregado Loan.
 */
@Entity
@Getter
@NoArgsConstructor
public class ScheduleEntry extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int installmentNumber; // Número de cuota (1, 2, 3...)
    private LocalDate dueDate;     // Fecha de vencimiento

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "principal_amount"))
    private Money principal;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest_amount"))
    private Money interest;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "fee_amount"))
    private Money fee;            // Comisión o cargo fijo (si aplica)

    private boolean isPaid;

    public ScheduleEntry(int installmentNumber, LocalDate dueDate, Money principal, Money interest, Money fee) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interest = interest;
        this.fee = fee;
        this.isPaid = false;
    }

    // Metodo de negocio para registrar el pago
    public void markAsPaid() {
        this.isPaid = true;
    }

    // Metodo auxiliar para obtener el total de la cuota
    public Money getTotalAmount() {
        return principal.add(interest).add(fee);
    }
}