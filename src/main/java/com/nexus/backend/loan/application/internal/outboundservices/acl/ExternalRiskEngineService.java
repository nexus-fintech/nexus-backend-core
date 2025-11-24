package com.nexus.backend.loan.application.internal.outboundservices.acl;

import com.nexus.backend.loan.infrastructure.outbound.external.risk.client.RiskEngineClient;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditRequestDto;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Anti-Corruption Layer (ACL) for the Risk Engine.
 * Translates Loan domain objects into the DTOs required by the Python microservice.
 */
@Service
@RequiredArgsConstructor
public class ExternalRiskEngineService {

    private final RiskEngineClient riskEngineClient;

    /**
     * Evaluates the risk of a loan request by communicating with the external microservice.
     *
     * @param clientId ID of the client
     * @param amount   Requested loan amount
     * @param term     Loan term in months
     * @param age      Client age (Required by Python)
     * @param income   Client monthly income (Required by Python)
     * @param debt     Client monthly debt (Required by Python)
     * @return The evaluation result (DTO).
     */
    public CreditScoreDto evaluateLoanRisk(Long clientId, BigDecimal amount, int term, int age, double income, double debt) {

        // Translation: Java Domain -> External DTO (Python)
        var requestDto = new CreditRequestDto(
                clientId,
                age,
                income,
                debt,
                amount.doubleValue(),
                term
        );

        // External Communication
        return riskEngineClient.evaluateRisk(requestDto);
    }
}