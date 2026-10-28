package com.nexus.backend.iam.infrastructure.hashing.bcrypt.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashingServiceImplTest {

    private final HashingServiceImpl hashing = new HashingServiceImpl();

    @Test
    void passwordsAreHashedWithBcrypt() {
        var hash = hashing.encode("Secreta123");

        assertThat(hash).isNotEqualTo("Secreta123").startsWith("$2");
        assertThat(hashing.matches("Secreta123", hash)).isTrue();
        assertThat(hashing.matches("otra-clave", hash)).isFalse();
    }

    @Test
    void eachHashCarriesItsOwnSalt() {
        assertThat(hashing.encode("Secreta123")).isNotEqualTo(hashing.encode("Secreta123"));
    }
}
