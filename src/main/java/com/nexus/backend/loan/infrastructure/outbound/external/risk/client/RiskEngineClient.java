package com.nexus.backend.loan.infrastructure.outbound.external.risk.client;

import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditRequestDto;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditScoreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "risk-engine", url = "${risk.engine.url:http://localhost:8000/api/v1}")
public interface RiskEngineClient {

    @PostMapping("/evaluate-risk")
    CreditScoreDto evaluateRisk(@RequestBody CreditRequestDto request);
}