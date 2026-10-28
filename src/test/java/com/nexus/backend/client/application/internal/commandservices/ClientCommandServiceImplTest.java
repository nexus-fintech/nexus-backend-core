package com.nexus.backend.client.application.internal.commandservices;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.commands.CreateClientCommand;
import com.nexus.backend.client.domain.model.commands.UpdateClientCommand;
import com.nexus.backend.client.domain.model.valueobjects.Dni;
import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;
import com.nexus.backend.client.infrastructure.persistence.jpa.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientCommandServiceImplTest {

    private static final CreateClientCommand ANA = new CreateClientCommand(
            "Ana", "Torres", "ana@example.com", "12345678", "Av. Arequipa 123", "Lima", "15001", "Perú", 5L);

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientCommandServiceImpl service;

    @Test
    void createsTheClientLinkedToItsUser() {
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var client = service.handle(ANA).orElseThrow();

        assertThat(client.getName().getFullName()).isEqualTo("Ana Torres");
        assertThat(client.getEmail().address()).isEqualTo("ana@example.com");
        assertThat(client.getDni().number()).isEqualTo("12345678");
        assertThat(client.getUserId()).isEqualTo(5L);
    }

    @Test
    void rejectsAnEmailThatIsAlreadyRegistered() {
        when(clientRepository.existsByEmail(new EmailAddress("ana@example.com"))).thenReturn(true);

        assertThatThrownBy(() -> service.handle(ANA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
        verify(clientRepository, never()).save(any());
    }

    @Test
    void rejectsADniThatIsAlreadyRegistered() {
        when(clientRepository.existsByDni(new Dni("12345678"))).thenReturn(true);

        assertThatThrownBy(() -> service.handle(ANA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DNI");
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updatingAnUnknownClientReturnsEmpty() {
        when(clientRepository.findById(42L)).thenReturn(Optional.empty());

        var update = new UpdateClientCommand(42L, "Ana", "Torres", "ana@example.com", "12345678",
                "Av. Arequipa 123", "Lima", "15001", "Perú");

        assertThat(service.handle(update)).isEmpty();
        verify(clientRepository, never()).save(any());
    }
}
