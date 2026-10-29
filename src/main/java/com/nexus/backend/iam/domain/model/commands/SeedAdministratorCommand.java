package com.nexus.backend.iam.domain.model.commands;

/**
 * Command to create the first administrator at startup, from the deployment's configuration.
 * The public sign-up only creates clients, so this is the only way an administrator comes to be.
 *
 * @param username the administrator's username
 * @param password the administrator's password
 */
public record SeedAdministratorCommand(String username, String password) {
}
