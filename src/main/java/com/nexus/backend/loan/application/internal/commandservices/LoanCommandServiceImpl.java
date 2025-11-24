package com.nexus.backend.loan.application.internal.commandservices;

import com.nexus.backend.loan.application.internal.outboundservices.acl.ExternalRiskEngineService;
import com.nexus.backend.loan.domain.model.aggregates.Loan;
import com.nexus.backend.loan.domain.model.commands.ApproveLoanCommand;
import com.nexus.backend.loan.domain.model.commands.RequestLoanCommand;
import com.nexus.backend.loan.domain.model.entities.ScheduleEntry;
import com.nexus.backend.loan.domain.model.valueobjects.Money;
import com.nexus.backend.loan.domain.services.LoanCommandService;
import com.nexus.backend.loan.infrastructure.persistence.jpa.repositories.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanCommandServiceImpl implements LoanCommandService {

    private final LoanRepository loanRepository;
    private final ExternalRiskEngineService riskEngineService; // Inyectamos la ACL

    @Override
    public Optional<Loan> handle(RequestLoanCommand command) {
        var loan = new Loan(command);
        var createdLoan = loanRepository.save(loan);
        return Optional.of(createdLoan);
    }

    /**
     * Intelligent Approval Flow:
     * 1. Retrieve the loan.
     * 2. Query the Risk Engine (Python).
     * 3. If approved -> Generate schedule and ACTIVATE.
     * 4. If rejected -> Mark as REJECTED.
     */
    @Override
    @Transactional
    public Optional<Loan> handle(ApproveLoanCommand command) {
        var loanOptional = loanRepository.findById(command.loanId());
        if (loanOptional.isEmpty()) return Optional.empty();

        var loan = loanOptional.get();

        // STEP 1: Risk Evaluation (Call to Python via ACL)
        var riskResult = riskEngineService.evaluateLoanRisk(
                loan.getClientId(),
                loan.getRequestedAmount().getAmount(),
                loan.getTermInMonths(),
                command.clientAge(),
                command.monthlyIncome(),
                command.monthlyDebt()
        );

        // STEP 2: AI-Based Decision-Making
        if (!riskResult.isApproved()) {
            // Very high risk: automatically reject the loan
            loan.reject();
            loanRepository.save(loan);
            // We could throw an exception, but returning the REJECTED object is more informative
            return Optional.of(loan);
        }

        // STEP 3: If approved, calculate amortization (French System)
        List<ScheduleEntry> schedule = calculateFrenchAmortizationSchedule(
                loan.getRequestedAmount().getAmount(),
                loan.getAnnualInterestRate().getRate(),
                loan.getTermInMonths()
        );

        // STEP 4: Approval and Activation
        loan.approve(schedule);
        var approvedLoan = loanRepository.save(loan);

        return Optional.of(approvedLoan);
    }

    // --- Mathematical Helper (Private) ---
    private List<ScheduleEntry> calculateFrenchAmortizationSchedule(BigDecimal principal, BigDecimal annualRate, int months) {
        List<ScheduleEntry> schedule = new ArrayList<>();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        BigDecimal onePlusRateToN = monthlyRate.add(BigDecimal.ONE).pow(months);

        // Fixed Payment Formula: P * (i * (1+i)^n) / ((1+i)^n - 1)
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRateToN);
        BigDecimal denominator = onePlusRateToN.subtract(BigDecimal.ONE);
        BigDecimal fixedMonthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        BigDecimal remainingPrincipal = principal;
        LocalDate nextDueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= months; i++) {
            BigDecimal interestPayment = remainingPrincipal.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment = fixedMonthlyPayment.subtract(interestPayment);

            if (i == months) {
                principalPayment = remainingPrincipal;
                fixedMonthlyPayment = principalPayment.add(interestPayment);
            }

            remainingPrincipal = remainingPrincipal.subtract(principalPayment);

            schedule.add(new ScheduleEntry(
                    i,
                    nextDueDate,
                    new Money(principalPayment),
                    new Money(interestPayment),
                    new Money(BigDecimal.ZERO)
            ));
            nextDueDate = nextDueDate.plusMonths(1);
        }
        return schedule;
    }
}