package com.nexus.backend.iam.domain.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import com.nexus.backend.iam.domain.model.aggregates.User;
import com.nexus.backend.iam.domain.model.commands.SignInCommand;
import com.nexus.backend.iam.domain.model.commands.SignUpCommand;

import java.util.Optional;

/**
 * Service interface for handling user-related commands such as sign-in and sign-up.
 */
public interface UserCommandService {

  /**
   * Handles the user sign-in process.
   *
   * @param command the sign-in command containing user credentials
   * @return an Optional containing a pair of User and authentication token if successful,
   *     or empty if the sign-in failed
   */
  Optional<ImmutablePair<User, String>> handle(SignInCommand command);

  /**
   * Handles the user sign-up process.
   *
   * @param command the sign-up command containing user details
   * @return an Optional containing the created User if successful, or empty if the sign-up failed
   */
  Optional<User> handle(SignUpCommand command);
}
