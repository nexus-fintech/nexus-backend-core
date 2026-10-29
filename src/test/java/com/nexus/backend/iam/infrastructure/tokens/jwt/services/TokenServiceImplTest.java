package com.nexus.backend.iam.infrastructure.tokens.jwt.services;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceImplTest {

    @Test
    void refusesToStartWithoutASecretOrWithOneTooShortForHs256() {
        assertThatThrownBy(() -> tokenService(null).requireStrongSecret())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
        assertThatThrownBy(() -> tokenService("31-bytes-is-one-short-of-enough").requireStrongSecret())
                .isInstanceOf(IllegalStateException.class);
        assertThatCode(() -> tokenService(SECRET).requireStrongSecret()).doesNotThrowAnyException();
    }

    private static final String SECRET = "nexus-backend-core-test-secret-0123456789abcdef";

    private static TokenServiceImpl tokenService(String secret) {
        var service = new TokenServiceImpl();
        ReflectionTestUtils.setField(service, "secret", secret);
        ReflectionTestUtils.setField(service, "expirationDays", 7);
        return service;
    }

    @Test
    void aTokenCarriesTheUsernameAndValidates() {
        var tokens = tokenService(SECRET);

        var token = tokens.generateToken("ana");

        assertThat(tokens.validateToken(token)).isTrue();
        assertThat(tokens.getUsernameFromToken(token)).isEqualTo("ana");
    }

    @Test
    void aTokenSignedWithAnotherKeyIsRejected() {
        var foreign = tokenService("a-completely-different-secret-for-the-same-app-000").generateToken("ana");

        assertThat(tokenService(SECRET).validateToken(foreign)).isFalse();
    }

    @Test
    void aTamperedOrMalformedTokenIsRejected() {
        var tokens = tokenService(SECRET);
        var token = tokens.generateToken("ana");
        var tampered = token.substring(0, token.length() - 4) + (token.endsWith("AAAA") ? "BBBB" : "AAAA");

        assertThat(tokens.validateToken(tampered)).isFalse();
        assertThat(tokens.validateToken("not-a-token")).isFalse();
    }
}
