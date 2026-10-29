package com.nexus.backend.client.interfaces.acl;

import com.nexus.backend.client.domain.model.aggregates.Client;
import com.nexus.backend.client.domain.model.queries.GetClientByIdQuery;
import com.nexus.backend.client.domain.services.ClientQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Client context facade: what other bounded contexts may ask about clients, without reaching
 * into the client's model.
 */
@Service
public class ClientContextFacade {

    private final ClientQueryService clientQueryService;

    public ClientContextFacade(ClientQueryService clientQueryService) {
        this.clientQueryService = clientQueryService;
    }

    /**
     * The IAM user that owns a client profile.
     *
     * @param clientId the client's id
     * @return the owner's user id, or empty if the client does not exist
     */
    public Optional<Long> fetchUserIdByClientId(Long clientId) {
        if (clientId == null) {
            return Optional.empty();
        }
        return clientQueryService.handle(new GetClientByIdQuery(clientId)).map(Client::getUserId);
    }
}
