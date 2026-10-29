package com.nexus.backend.loan.application.internal.outboundservices.acl;

import com.nexus.backend.client.interfaces.acl.ClientContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-corruption layer towards the Client context: the loan context asks about clients only here.
 */
@Service
@RequiredArgsConstructor
public class ExternalClientService {

    private final ClientContextFacade clientContextFacade;

    /**
     * The IAM user that owns a client.
     *
     * @param clientId the client's id
     * @return the owner's user id, or empty if the client does not exist
     */
    public Optional<Long> fetchOwnerUserId(Long clientId) {
        return clientContextFacade.fetchUserIdByClientId(clientId);
    }

    /**
     * Whether a client with this id exists.
     *
     * @param clientId the client's id
     * @return true if the client exists
     */
    public boolean existsClient(Long clientId) {
        return fetchOwnerUserId(clientId).isPresent();
    }
}
