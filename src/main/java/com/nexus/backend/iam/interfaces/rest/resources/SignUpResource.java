package com.nexus.backend.iam.interfaces.rest.resources;

/**
 * Resource representing a sign-up request.
 *
 * <p>It carries no roles: the public sign-up always creates a client, so nobody can make
 * themselves an administrator. A "roles" field sent by an older client is ignored.</p>
 *
 * @param username the username for the new user
 * @param password the password for the new user
 */
public record SignUpResource(String username, String password) {
}
