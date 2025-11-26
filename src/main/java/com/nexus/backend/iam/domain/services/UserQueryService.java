package com.nexus.backend.iam.domain.services;

import com.nexus.backend.iam.domain.model.aggregates.User;
import com.nexus.backend.iam.domain.model.queries.GetAllUsersQuery;
import com.nexus.backend.iam.domain.model.queries.GetUserByIdQuery;
import com.nexus.backend.iam.domain.model.queries.GetUserByUsernameQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling user-related queries.
 */
public interface UserQueryService {

  /**
   * Handle the query to get all users.
   *
   * @param query The query to get all users.
   * @return A list of all users.
   */
  List<User> handle(GetAllUsersQuery query);

  /**
   * Handle the query to get a user by their ID.
   *
   * @param query The query containing the user ID.
   * @return An Optional containing the User if found, or empty if not found.
   */
  Optional<User> handle(GetUserByIdQuery query);

  /**
   * Handle the query to get a user by their username.
   *
   * @param query The query containing the username.
   * @return An Optional containing the User if found, or empty if not found.
   */
  Optional<User> handle(GetUserByUsernameQuery query);
}
