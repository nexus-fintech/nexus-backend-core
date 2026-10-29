package com.nexus.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.backend.loan.infrastructure.outbound.external.risk.client.RiskEngineClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The public sign-up only creates clients, so the first administrator comes from the deployment's
 * configuration (NEXUS_ADMIN_USERNAME, NEXUS_ADMIN_PASSWORD). Its own database keeps it apart from
 * the other API tests.
 */
@SpringBootTest(properties = {
        "nexus.admin.username=primer-admin",
        "nexus.admin.password=Primera-Clave-2026",
        "spring.datasource.url=jdbc:h2:mem:nexusdb-admin;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FirstAdministratorTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @MockitoBean
    private RiskEngineClient riskEngineClient;

    @Test
    void theConfiguredAdministratorExistsFromStartupAndManagesUsers() throws Exception {
        var signIn = mvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "username", "primer-admin", "password", "Primera-Clave-2026"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", contains("ROLE_ADMIN")))
                .andReturn();
        var bearer = "Bearer " + json.readTree(signIn.getResponse().getContentAsString()).get("token").asText();

        mvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isOk());
    }
}
