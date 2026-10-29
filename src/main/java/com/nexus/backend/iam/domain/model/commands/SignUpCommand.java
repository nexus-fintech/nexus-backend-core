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

  /** Shortest password accepted. */
  public static final int MIN_PASSWORD_LENGTH = 8;

  /** Longest username the users table stores. */
  public static final int MAX_USERNAME_LENGTH = 50;

  /**
   * Validates the credentials before anything is stored.
   *
   * @throws IllegalArgumentException if the username is blank or too long, or the password too short
   */
  public SignUpCommand {
    if (username == null || username.isBlank() || username.length() > MAX_USERNAME_LENGTH) {
      throw new IllegalArgumentException(
          "Username is required and must have at most " + MAX_USERNAME_LENGTH + " characters");
    }
    if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "Password must have at least " + MIN_PASSWORD_LENGTH + " characters");
    }
  }
}
