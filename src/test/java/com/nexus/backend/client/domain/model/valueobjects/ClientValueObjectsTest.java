package com.nexus.backend.client.domain.model.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientValueObjectsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void dniAndEmailRequireAValue(String blank) {
        assertThatThrownBy(() -> new Dni(blank)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new EmailAddress(blank)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void personNameRequiresFirstAndLastName(String blank) {
        assertThatThrownBy(() -> new PersonName(blank, "Torres")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PersonName("Ana", blank)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void personNameBuildsTheFullName() {
        assertThat(new PersonName("Ana", "Torres").getFullName()).isEqualTo("Ana Torres");
    }

    @Test
    void streetAddressRequiresStreetCityAndCountry() {
        assertThatCode(() -> new StreetAddress("Av. Arequipa 123", "Lima", "15001", "Perú")).doesNotThrowAnyException();
        assertThatThrownBy(() -> new StreetAddress(null, "Lima", "15001", "Perú")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StreetAddress("Av. Arequipa 123", null, "15001", "Perú")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StreetAddress("Av. Arequipa 123", "Lima", "15001", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
