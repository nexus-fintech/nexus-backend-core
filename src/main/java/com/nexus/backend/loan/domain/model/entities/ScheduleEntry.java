package com.nexus.backend.loan.domain.model.entities;

import com.nexus.backend.loan.domain.model.valueobjects.Money;
import com.nexus.backend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Money principal;       // Cantidad de capital
    @Embedded
    private Money interest;        // Cantidad de interés

    @Embedded
    private Money fee;             // Comisión o cargo fijo (si aplica)

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