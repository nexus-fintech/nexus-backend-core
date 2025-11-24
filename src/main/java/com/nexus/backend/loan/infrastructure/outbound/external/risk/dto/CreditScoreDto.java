package com.nexus.backend.loan.infrastructure.outbound.external.risk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreditScoreDto(
        @JsonProperty("score") int score,
        @JsonProperty("risk_level") String riskLevel,
        @JsonProperty("is_approved") boolean isApproved,
        @JsonProperty("suggested_interest_rate") double suggestedInterestRate,
        @JsonProperty("max_approved_amount") double maxApprovedAmount
) {}