package com.nexus.backend.loan.domain.model.aggregates;

import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.domain.model.entities.ScheduleEntry;
import com.nexus.backend.loan.domain.model.valueobjects.InterestRate;
import com.nexus.backend.loan.domain.model.valueobjects.LoanStatus;
import com.nexus.backend.loan.domain.model.valueobjects.Money;
import com.nexus.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Agregado Principal del Bounded Context de Préstamos (Loan).
 * Controla el ciclo de vida del crédito, el estado y el cronograma de pagos.
 */
@Entity
@Getter
@NoArgsConstructor // Requerido por JPA
public class Loan extends AuditableAbstractAggregateRoot<Loan> {

    // Referencia al Agregado Cliente (Foreign Key - NO la entidad completa)
    @Column(nullable = false)
    private Long clientId;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "amount", column = @Column(name = "requested_amount"))})
    private Money requestedAmount;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "rate", column = @Column(name = "annual_interest_rate"))})
    private InterestRate annualInterestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false)
    private int termInMonths; // Plazo en meses

    private LocalDate disbursementDate; // Fecha de desembolso (solo si está ACTIVO)

    // Entidades internas (Tabla de Amortización)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "loan_id", nullable = false)
    private List<ScheduleEntry> scheduleEntries = new ArrayList<>();

    public Loan(RequestLoanCommand command) {
        this.clientId = command.clientId();
        this.requestedAmount = new Money(command.requestedAmount());
        this.annualInterestRate = new InterestRate(command.annualInterestRate());
        this.termInMonths = command.termInMonths();
        this.status = LoanStatus.REQUESTED;
        // La fecha de desembolso es null hasta que se APRUEBA
    }

    /**
     * Metodo de Negocio: Marca el préstamo como APROBADO y define el cronograma.
     * @param schedule Cuotas generadas por el Domain Service.
     */
    public void approve(List<ScheduleEntry> schedule) {
        if (this.status != LoanStatus.REQUESTED) {
            throw new IllegalStateException("Only requested loans can be approved.");
        }
        this.status = LoanStatus.ACTIVE;
        this.disbursementDate = LocalDate.now();
        this.scheduleEntries.addAll(schedule);
        // Aquí se registraría un Domain Event: LoanApprovedEvent
    }

    // Aquí irían otros métodos de negocio: reject(), registerPayment(), checkDefault(), etc.
}