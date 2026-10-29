package com.nexus.backend;

import com.nexus.backend.loan.infrastructure.outbound.external.risk.client.RiskEngineClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Over a real HTTP server, not MockMvc: Spring Boot forwards an error to /error, and that forward
 * goes through the security filters again. If /error needed a session, every malformed request
 * from someone not signed in would answer 401 instead of 400. Its own database keeps it apart.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties =
        "spring.datasource.url=jdbc:h2:mem:nexusdb-http;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1")
@ActiveProfiles("test")
class ErrorResponsesOverHttpTest {

    private final HttpClient http = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @MockitoBean
    private RiskEngineClient riskEngineClient;

    private int statusOf(HttpRequest.Builder request) throws Exception {
        return http.send(request.build(), HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private HttpRequest.Builder post(String path, String body) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
    }

    @Test
    void aMalformedBodyIsA400NotA401() throws Exception {
        assertThat(statusOf(post("/api/v1/authentication/sign-up", "{roto"))).isEqualTo(400);
        assertThat(statusOf(post("/api/v1/authentication/sign-in", ""))).isEqualTo(400);
    }

    @Test
    void aProtectedEndpointWithoutASessionIsStillA401() throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/clients")).GET();

        assertThat(statusOf(request)).isEqualTo(401);
    }
}
