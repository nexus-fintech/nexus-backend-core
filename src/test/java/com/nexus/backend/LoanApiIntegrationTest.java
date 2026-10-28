package com.nexus.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.backend.iam.domain.model.commands.SignUpCommand;
import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.domain.model.valueobjects.Roles;
import com.nexus.backend.iam.domain.services.UserCommandService;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.client.RiskEngineClient;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditRequestDto;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.dto.CreditScoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end over HTTP with the real security filter chain and an in-memory database.
 * Only the external risk engine (Python) is replaced by a mock.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanApiIntegrationTest {

    private static final String LOANS = "/api/v1/loans";
    private static final AtomicInteger NEXT_DNI = new AtomicInteger(20_000_000);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private RiskEngineClient riskEngineClient;

    private String bearer;
    private long clientId;

    @BeforeEach
    void anAdminSignedInWithAClient() throws Exception {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        var username = "admin-" + suffix;
        // The admin is created through the service, not the public sign-up. Inside an HTTP request
        // open-in-view keeps the roles attached; out here a transaction does the same job.
        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                userCommandService.handle(new SignUpCommand(username, "Secreta123", List.of(new Role(Roles.ROLE_ADMIN)))));

        var signIn = mvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("username", username, "password", "Secreta123"))))
                .andExpect(status().isOk())
                .andReturn();
        bearer = "Bearer " + json.readTree(signIn.getResponse().getContentAsString()).get("token").asText();

        var client = mvc.perform(post("/api/v1/clients")
                        .header(HttpHeaders.AUTHORIZATION, bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "firstName", "Ana", "lastName", "Torres",
                                "email", "ana-" + suffix + "@example.com",
                                "dni", String.valueOf(NEXT_DNI.incrementAndGet()),
                                "street", "Av. Arequipa 123", "city", "Lima", "zipCode", "15001", "country", "Perú"))))
                .andExpect(status().isCreated())
                .andReturn();
        clientId = json.readTree(client.getResponse().getContentAsString()).get("id").asLong();
    }

    private ResultActions requestLoan(Object amount, int months, double annualRate) throws Exception {
        return mvc.perform(post(LOANS)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                        "clientId", clientId, "amount", amount, "termInMonths", months, "annualInterestRate", annualRate))));
    }

    private long requestedLoanId(String amount, int months, double annualRate) throws Exception {
        var response = requestLoan(new BigDecimal(amount), months, annualRate)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andReturn();
        return json.readTree(response.getResponse().getContentAsString()).get("id").asLong();
    }

    private ResultActions approve(long loanId, int clientAge) throws Exception {
        return mvc.perform(post(LOANS + "/" + loanId + "/approve")
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("clientAge", clientAge, "monthlyIncome", 5000, "monthlyDebt", 500))));
    }

    private void riskEngineDecides(boolean approved) {
        when(riskEngineClient.evaluateRisk(any())).thenReturn(
                new CreditScoreDto(approved ? 780 : 520, approved ? "LOW" : "HIGH", approved, 0.13, approved ? 50000 : 0));
    }

    @Test
    void anApprovedLoanIsActivatedAndStoredWithItsSchedule() throws Exception {
        riskEngineDecides(true);
        var loanId = requestedLoanId("6000", 6, 0.12);

        // The approval answer leaves the schedule out on purpose; GET /loans/{id} carries it
        approve(loanId, 30)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.disbursementDate").isNotEmpty());
        verify(riskEngineClient).evaluateRisk(new CreditRequestDto(clientId, 30, 5000.0, 500.0, 6000.0, 6));

        var stored = mvc.perform(get(LOANS + "/" + loanId).header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.schedule.length()").value(6))
                .andReturn();
        var principal = BigDecimal.ZERO;
        for (var installment : json.readTree(stored.getResponse().getContentAsString()).get("schedule")) {
            principal = principal.add(installment.get("principal").decimalValue());
        }
        assertThat(principal).isEqualByComparingTo("6000.00");
    }

    @Test
    void aHighRiskProfileIsRejectedWithoutSchedule() throws Exception {
        riskEngineDecides(false);
        var loanId = requestedLoanId("20000", 24, 0.20);

        approve(loanId, 30)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.schedule").isEmpty());
    }

    @Test
    void approvingTheSameLoanTwiceIsAConflict() throws Exception {
        riskEngineDecides(true);
        var loanId = requestedLoanId("3000", 3, 0.10);
        approve(loanId, 30).andExpect(status().isOk());

        approve(loanId, 30)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Only requested loans can be approved."));
    }

    @Test
    void anInvalidLoanRequestIsABadRequest() throws Exception {
        requestLoan(new BigDecimal("3000"), 0, 0.10)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Term must be at least one month"));
    }

    @Test
    void anUnderageApplicantIsRejectedBeforeAskingTheRiskEngine() throws Exception {
        var loanId = requestedLoanId("3000", 3, 0.10);

        approve(loanId, 17)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Client must be at least 18 years old"));
        verifyNoInteractions(riskEngineClient);
    }

    @Test
    void approvingAnUnknownLoanIsNotFound() throws Exception {
        approve(999_999L, 30).andExpect(status().isNotFound());
    }

    @Test
    void theLoanApiRequiresAToken() throws Exception {
        mvc.perform(post(LOANS).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
