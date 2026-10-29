package com.nexus.backend.client.interfaces.rest;

import com.nexus.backend.client.domain.model.queries.GetClientByIdQuery;
import com.nexus.backend.client.domain.services.ClientQueryService;
import com.nexus.backend.iam.interfaces.acl.IamContextFacade;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Ownership checks for the client endpoints, called from {@code @PreAuthorize}.
 *
 * <p>A client reaches only its own profile. An administrator is let through by its role before
 * this guard is asked.</p>
 */
@Component
public class ClientAccessGuard {

    private final ClientQueryService clientQueryService;
    private final IamContextFacade iamContextFacade;

    public ClientAccessGuard(ClientQueryService clientQueryService, IamContextFacade iamContextFacade) {
        this.clientQueryService = clientQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Whether the caller is the IAM user with this id.
     *
     * @param authentication the caller
     * @param userId the user id in the request
     * @return true only for the caller's own id
     */
    public boolean isCurrentUser(Authentication authentication, Long userId) {
        return userId != null && userId.equals(currentUserId(authentication));
    }

    /**
     * Whether the caller owns the client profile with this id.
     *
     * @param authentication the caller
     * @param clientId the client id in the request
     * @return true only for the caller's own profile; false if it does not exist
     */
    public boolean ownsClient(Authentication authentication, Long clientId) {
        if (clientId == null) {
            return false;
        }
        return clientQueryService.handle(new GetClientByIdQuery(clientId))
                .map(client -> Objects.equals(client.getUserId(), currentUserId(authentication)))
                .orElse(false);
    }

    private Long currentUserId(Authentication authentication) {
        return iamContextFacade.fetchUserIdByUsername(authentication.getName());
    }
}
