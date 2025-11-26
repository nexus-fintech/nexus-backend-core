package com.nexus.backend.iam.domain.model.commands;

/**
 * Command to sign in a user with username and password.
 *
 * @param username the username of the user
 * @param password the password of the user
 */
public record SignInCommand(String username, String password) {
}
