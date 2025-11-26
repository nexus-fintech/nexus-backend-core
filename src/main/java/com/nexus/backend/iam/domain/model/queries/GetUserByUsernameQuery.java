package com.nexus.backend.iam.domain.model.queries;

/**
 * Query to get a user by their username.
 *
 * @param username the username of the user to retrieve
 */
public record GetUserByUsernameQuery(String username) {
}
