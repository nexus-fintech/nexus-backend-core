package com.nexus.backend.loan.application.internal.outboundservices.acl;

import com.nexus.backend.loan.infrastructure.outbound.external.risk.client.RiskEngineClient;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditRequestDto;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditScoreDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalRiskEngineServiceTest {

    @Mock
    private RiskEngineClient riskEngineClient;

    @InjectMocks
    private ExternalRiskEngineService service;

    @Test
    void translatesTheLoanIntoTheRiskEngineContract() {
        var decision = new CreditScoreDto(700, "MEDIUM", true, 0.15, 40000);
        when(riskEngineClient.evaluateRisk(any())).thenReturn(decision);

        var result = service.evaluateLoanRisk(7L, new BigDecimal("12000.00"), 24, 30, 4000.0, 600.0);

        var request = ArgumentCaptor.forClass(CreditRequestDto.class);
        verify(riskEngineClient).evaluateRisk(request.capture());
        assertThat(request.getValue()).isEqualTo(new CreditRequestDto(7L, 30, 4000.0, 600.0, 12000.0, 24));
        assertThat(result).isSameAs(decision);
    }
}
