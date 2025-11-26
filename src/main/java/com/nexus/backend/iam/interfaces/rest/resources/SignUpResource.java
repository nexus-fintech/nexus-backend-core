package com.nexus.backend.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Resource representing a sign-up request.
 *
 * @param username the username for the new user
 * @param password the password for the new user
 * @param roles    the roles assigned to the new user
 */
public record SignUpResource(String username, String password, List<String> roles) {
}

