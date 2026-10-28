package com.nexus.backend.loan.domain.model.aggregates;

import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.domain.model.entities.ScheduleEntry;
import com.nexus.backend.loan.domain.model.valueobjects.LoanStatus;
import com.nexus.backend.loan.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanTest {

    private static Loan requestedLoan() {
        return new Loan(new RequestLoanCommand(7L, new BigDecimal("5000"), 12, 0.15));
    }

    @Test
    void startsRequestedWithoutScheduleOrDisbursement() {
        var loan = requestedLoan();

        assertThat(loan.getStatus()).isEqualTo(LoanStatus.REQUESTED);
        assertThat(loan.getClientId()).isEqualTo(7L);
        assertThat(loan.getRequestedAmount().getAmount()).isEqualByComparingTo("5000.00");
        assertThat(loan.getAnnualInterestRate().getRate()).isEqualByComparingTo("0.15");
        assertThat(loan.getTermInMonths()).isEqualTo(12);
        assertThat(loan.getScheduleEntries()).isEmpty();
        assertThat(loan.getDisbursementDate()).isNull();
    }

    @Test
    void approvingActivatesTheLoanAndAttachesTheSchedule() {
        var loan = requestedLoan();
        var installment = new ScheduleEntry(1, LocalDate.now().plusMonths(1),
                new Money(450.0), new Money(62.5), new Money(0.0));

        loan.approve(List.of(installment));

        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(loan.getDisbursementDate()).isEqualTo(LocalDate.now());
        assertThat(loan.getScheduleEntries()).containsExactly(installment);
    }

    @Test
    void rejectingMarksTheLoanRejected() {
        var loan = requestedLoan();

        loan.reject();

        assertThat(loan.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(loan.getScheduleEntries()).isEmpty();
    }

    @Test
    void onlyRequestedLoansCanBeApprovedOrRejected() {
        var approved = requestedLoan();
        approved.approve(List.of());
        assertThatThrownBy(() -> approved.approve(List.of())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(approved::reject).isInstanceOf(IllegalStateException.class);

        var rejected = requestedLoan();
        rejected.reject();
        assertThatThrownBy(() -> rejected.approve(List.of())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void anInstallmentTotalsPrincipalInterestAndFeeAndStartsUnpaid() {
        var installment = new ScheduleEntry(1, LocalDate.now(), new Money(450.0), new Money(62.5), new Money(5.0));

        assertThat(installment.getTotalAmount().getAmount()).isEqualByComparingTo("517.50");
        assertThat(installment.isPaid()).isFalse();
        installment.markAsPaid();
        assertThat(installment.isPaid()).isTrue();
    }
}
