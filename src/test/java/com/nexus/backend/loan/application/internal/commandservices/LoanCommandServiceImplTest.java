package com.nexus.backend.loan.application.internal.commandservices;

import com.nexus.backend.loan.application.internal.outboundservices.acl.ExternalRiskEngineService;
import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.commands.ApproveLoanCommand;
import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.domain.model.entities.ScheduleEntry;
import com.nexus.backend.loan.domain.model.valueobjects.LoanStatus;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditScoreDto;
import com.nexus.backend.loan.infrastructure.persistence.jpa.repositories.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanCommandServiceImplTest {

    private static final ApproveLoanCommand APPROVE = new ApproveLoanCommand(1L, 35, 4500.0, 800.0);

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ExternalRiskEngineService riskEngineService;

    @InjectMocks
    private LoanCommandServiceImpl service;

    private Loan storedLoan(String amount, int months, double annualRate) {
        var loan = new Loan(new RequestLoanCommand(7L, new BigDecimal(amount), months, annualRate));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        return loan;
    }

    private void riskEngineDecides(boolean approved) {
        var decision = new CreditScoreDto(approved ? 780 : 520, approved ? "LOW" : "HIGH", approved, 0.13, approved ? 45000 : 0);
        when(riskEngineService.evaluateLoanRisk(any(), any(), anyInt(), anyInt(), anyDouble(), anyDouble()))
                .thenReturn(decision);
    }

    private static BigDecimal totalPrincipal(List<ScheduleEntry> schedule) {
        return schedule.stream().map(entry -> entry.getPrincipal().getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    void requestingALoanStoresItAsRequestedWithoutCallingTheRiskEngine() {
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var loan = service.handle(new RequestLoanCommand(7L, new BigDecimal("3000"), 6, 0.20)).orElseThrow();

        assertThat(loan.getStatus()).isEqualTo(LoanStatus.REQUESTED);
        verify(loanRepository).save(loan);
        verifyNoInteractions(riskEngineService);
    }

    @Test
    void approvingAnUnknownLoanReturnsEmpty() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.handle(new ApproveLoanCommand(99L, 30, 4000, 500))).isEmpty();
        verifyNoInteractions(riskEngineService);
    }

    @Test
    void theRiskEngineReceivesTheLoanAndTheClientProfile() {
        storedLoan("10000", 12, 0.12);
        riskEngineDecides(true);

        service.handle(APPROVE);

        verify(riskEngineService).evaluateLoanRisk(7L, new BigDecimal("10000.00"), 12, 35, 4500.0, 800.0);
    }

    @Test
    void aHighRiskProfileRejectsTheLoanWithoutSchedule() {
        var loan = storedLoan("10000", 12, 0.12);
        riskEngineDecides(false);

        var result = service.handle(APPROVE).orElseThrow();

        assertThat(result.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(result.getScheduleEntries()).isEmpty();
        verify(loanRepository).save(loan);
    }

    @Test
    void anApprovedLoanGetsAFrenchAmortizationSchedule() {
        storedLoan("10000", 12, 0.12);
        riskEngineDecides(true);

        var result = service.handle(APPROVE).orElseThrow();
        var schedule = result.getScheduleEntries();

        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(schedule).hasSize(12);
        // 10,000 at 1% a month over 12 months: fixed installment of 888.49
        schedule.subList(0, 11).forEach(entry ->
                assertThat(entry.getTotalAmount().getAmount()).isEqualByComparingTo("888.49"));
        assertThat(schedule.get(0).getInterest().getAmount()).isEqualByComparingTo("100.00");
        assertThat(schedule.get(0).getPrincipal().getAmount()).isEqualByComparingTo("788.49");
        // The last installment absorbs the rounding, so the principal adds up exactly
        assertThat(totalPrincipal(schedule)).isEqualByComparingTo("10000.00");
        // Interest falls as the balance goes down
        assertThat(schedule.get(11).getInterest().getAmount()).isLessThan(schedule.get(0).getInterest().getAmount());
    }

    @Test
    void installmentsAreNumberedAndFallDueMonthlyFromToday() {
        storedLoan("2400", 6, 0.18);
        riskEngineDecides(true);

        var schedule = service.handle(APPROVE).orElseThrow().getScheduleEntries();
        var today = LocalDate.now();

        for (int i = 0; i < schedule.size(); i++) {
            assertThat(schedule.get(i).getInstallmentNumber()).isEqualTo(i + 1);
            assertThat(schedule.get(i).getDueDate()).isEqualTo(today.plusMonths(i + 1));
        }
    }

    @Test
    void aZeroRateLoanSplitsThePrincipalEvenly() {
        storedLoan("1200", 12, 0.0);
        riskEngineDecides(true);

        var schedule = service.handle(APPROVE).orElseThrow().getScheduleEntries();

        assertThat(schedule).hasSize(12).allSatisfy(entry -> {
            assertThat(entry.getPrincipal().getAmount()).isEqualByComparingTo("100.00");
            assertThat(entry.getInterest().getAmount()).isEqualByComparingTo("0.00");
        });
    }

    @Test
    void theLastInstallmentAbsorbsTheRoundingRemainder() {
        storedLoan("1000", 3, 0.0);
        riskEngineDecides(true);

        var schedule = service.handle(APPROVE).orElseThrow().getScheduleEntries();

        assertThat(schedule).extracting(entry -> entry.getPrincipal().getAmount().toPlainString())
                .containsExactly("333.33", "333.33", "333.34");
    }
}
