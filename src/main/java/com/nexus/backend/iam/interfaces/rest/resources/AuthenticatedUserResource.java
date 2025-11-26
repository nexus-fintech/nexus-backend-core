package com.nexus.backend.iam.interfaces.rest.resources;

/**
 * AuthenticatedUserResource record.
 * This record is used to represent an authenticated user.
 *
 * @param id       the id of the authenticated user
 * @param username the username of the authenticated user
 * @param token    the token of the authenticated user
 */
public record AuthenticatedUserResource(Long id, String username, String token) {
}
