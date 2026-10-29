package com.nexus.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.backend.iam.domain.model.commands.SignUpCommand;
import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.domain.model.valueobjects.Roles;
import com.nexus.backend.iam.domain.services.UserCommandService;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.client.RiskEngineClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The security rules over HTTP, with the real filter chain and an in-memory database: who can sign
 * up as what, how a failed sign-in answers, and that a client only reaches its own data.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class SecurityApiIntegrationTest {

    private static final String PASSWORD = "Secreta123";
    private static final AtomicInteger NEXT_DNI = new AtomicInteger(30_000_000);

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

    /** Someone who signed up through the public endpoint and created a client profile. */
    private record Customer(long userId, String bearer, long clientId) {
    }

    private static String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private ResultActions signUp(Map<String, ?> request) throws Exception {
        return mvc.perform(post("/api/v1/authentication/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request)));
    }

    private ResultActions signIn(String username, String password) throws Exception {
        return mvc.perform(post("/api/v1/authentication/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("username", username, "password", password))));
    }

    private String bearerFor(String username) throws Exception {
        var response = signIn(username, PASSWORD).andExpect(status().isOk()).andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("token").asText();
    }

    private String administrator() throws Exception {
        var username = unique("admin");
        // Administrators never come from the public sign-up: the test creates one through the service
        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                userCommandService.handle(new SignUpCommand(username, PASSWORD, List.of(new Role(Roles.ROLE_ADMIN)))));
        return bearerFor(username);
    }

    private Customer customer() throws Exception {
        var username = unique("cliente");
        var user = signUp(Map.of("username", username, "password", PASSWORD))
                .andExpect(status().isCreated())
                .andReturn();
        var userId = json.readTree(user.getResponse().getContentAsString()).get("id").asLong();
        var bearer = bearerFor(username);
        var profile = mvc.perform(post("/api/v1/clients")
                        .header(HttpHeaders.AUTHORIZATION, bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(profileOf(username))))
                .andExpect(status().isCreated())
                .andReturn();
        var clientId = json.readTree(profile.getResponse().getContentAsString()).get("id").asLong();
        return new Customer(userId, bearer, clientId);
    }

    private static Map<String, String> profileOf(String username) {
        return Map.of(
                "firstName", "Ana", "lastName", "Torres", "email", username + "@example.com",
                "dni", String.valueOf(NEXT_DNI.incrementAndGet()),
                "street", "Av. Arequipa 123", "city", "Lima", "zipCode", "15001", "country", "Perú");
    }

    private ResultActions requestLoan(String bearer, long clientId) throws Exception {
        return mvc.perform(post("/api/v1/loans")
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                        "clientId", clientId, "amount", 3000, "termInMonths", 6, "annualInterestRate", 0.2))));
    }

    private long loanOf(Customer customer) throws Exception {
        var loan = requestLoan(customer.bearer(), customer.clientId()).andExpect(status().isCreated()).andReturn();
        return json.readTree(loan.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void theSignUpCreatesAClientEvenWhenItAsksToBeAnAdministrator() throws Exception {
        var username = unique("intruso");

        signUp(Map.of("username", username, "password", PASSWORD, "roles", List.of("ROLE_ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roles", contains("ROLE_CLIENT")));

        mvc.perform(get("/api/v1/clients").header(HttpHeaders.AUTHORIZATION, bearerFor(username)))
                .andExpect(status().isForbidden());
    }

    @Test
    void theSignUpRejectsAShortPasswordAndATakenUsername() throws Exception {
        signUp(Map.of("username", unique("corta"), "password", "1234567"))
                .andExpect(status().isBadRequest());

        var username = unique("repetido");
        signUp(Map.of("username", username, "password", PASSWORD)).andExpect(status().isCreated());
        signUp(Map.of("username", username, "password", PASSWORD)).andExpect(status().isConflict());
    }

    @Test
    void aFailedSignInIsA401ThatDoesNotSayWhetherTheUserExists() throws Exception {
        var username = unique("ana");
        signUp(Map.of("username", username, "password", PASSWORD)).andExpect(status().isCreated());

        var wrongPassword = signIn(username, "otra-clave-123").andExpect(status().isUnauthorized()).andReturn();
        var unknownUser = signIn(unique("nadie"), PASSWORD).andExpect(status().isUnauthorized()).andReturn();

        assertThat(wrongPassword.getResponse().getContentAsString())
                .isEqualTo(unknownUser.getResponse().getContentAsString())
                .contains("Invalid username or password");
    }

    @Test
    void aClientReachesItsOwnProfileAndLoans() throws Exception {
        var ana = customer();
        loanOf(ana);

        mvc.perform(get("/api/v1/clients/{id}", ana.clientId()).header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/clients/by-user/{id}", ana.userId()).header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/loans").param("clientId", String.valueOf(ana.clientId()))
                        .header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void aClientCannotReadChangeOrBorrowInSomeoneElsesName() throws Exception {
        var ana = customer();
        var luis = customer();
        var luisLoan = loanOf(luis);

        mvc.perform(get("/api/v1/clients/{id}", luis.clientId()).header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/clients/by-user/{id}", luis.userId()).header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/clients/{id}", luis.clientId())
                        .header(HttpHeaders.AUTHORIZATION, ana.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(profileOf(unique("luis")))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/loans").param("clientId", String.valueOf(luis.clientId()))
                        .header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/loans/{id}", luisLoan).header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isForbidden());
        requestLoan(ana.bearer(), luis.clientId()).andExpect(status().isForbidden());
    }

    @Test
    void onlyAnAdministratorListsUsersAndRoles() throws Exception {
        var ana = customer();
        var admin = administrator();

        mvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/roles").header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, admin))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/roles").header(HttpHeaders.AUTHORIZATION, admin))
                .andExpect(status().isOk());
    }

    @Test
    void noOneCanRequestALoanForAClientThatDoesNotExist() throws Exception {
        requestLoan(administrator(), 987_654_321L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("does not exist")));
    }

    @Test
    void theBearerTokenNeverReachesTheLogs(CapturedOutput output) throws Exception {
        var ana = customer();

        mvc.perform(get("/api/v1/clients/{id}", ana.clientId()).header(HttpHeaders.AUTHORIZATION, ana.bearer()))
                .andExpect(status().isOk());

        assertThat(output.getAll()).doesNotContain(ana.bearer().substring("Bearer ".length()));
    }

    @Test
    void onlyTheHealthCheckOfActuatorIsPublic() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/env")).andExpect(status().isUnauthorized());
    }

    @Test
    void browsersFromOriginsNotConfiguredAreTurnedAway() throws Exception {
        mvc.perform(options("/api/v1/loans")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"));
        mvc.perform(options("/api/v1/loans")
                        .header(HttpHeaders.ORIGIN, "https://evil.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden());
    }
}
