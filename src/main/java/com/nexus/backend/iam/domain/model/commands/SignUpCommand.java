package com.nexus.backend.iam.domain.model.commands;

import com.nexus.backend.iam.domain.model.entities.Role;

import java.util.List;

/**
 * Command to sign up a new user with specified username, password, and roles.
 *
 * @param username the username of the new user
 * @param password the password of the new user
 * @param roles    the list of roles assigned to the new user
 */
public record SignUpCommand(String username, String password, List<Role> roles) {
}