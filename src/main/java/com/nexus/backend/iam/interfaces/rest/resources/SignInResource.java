package com.nexus.backend.iam.interfaces.rest.resources;

/**
 * Resource representing sign-in credentials.
 *
 * @param username the username of the user
 * @param password the password of the user
 */
public record SignInResource(String username, String password) {
}
