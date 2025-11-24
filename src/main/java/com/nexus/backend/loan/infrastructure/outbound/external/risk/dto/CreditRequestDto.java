package com.nexus.backend.loan.infrastructure.outbound.external.risk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreditRequestDto(
        @JsonProperty("client_id") Long clientId,
        @JsonProperty("age") int age,
        @JsonProperty("monthly_income") double monthlyIncome,
        @JsonProperty("monthly_debt") double monthlyDebt,
        @JsonProperty("requested_amount") double requestedAmount,
        @JsonProperty("term_in_months") int termInMonths
) {}