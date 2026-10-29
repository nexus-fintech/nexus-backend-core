package com.nexus.backend.iam.application.internal.commandservices;

import com.nexus.backend.iam.application.internal.outboundservices.hashing.HashingService;
import com.nexus.backend.iam.application.internal.outboundservices.tokens.TokenService;
import com.nexus.backend.iam.domain.model.aggregates.User;
import com.nexus.backend.iam.domain.model.commands.SeedAdministratorCommand;
import com.nexus.backend.iam.domain.model.commands.SignInCommand;
import com.nexus.backend.iam.domain.model.commands.SignUpCommand;
import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.domain.model.valueobjects.Roles;
import com.nexus.backend.iam.domain.services.UserCommandService;
import com.nexus.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.nexus.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User command service implementation.
 *
 * <p>This class implements the {@link UserCommandService} interface and provides
 *     the implementation for the {@link SignInCommand} and {@link SignUpCommand} commands.
 * </p>
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

  /** The answer to every failed sign-in: it never tells whether the username exists. */
  static final String INVALID_CREDENTIALS = "Invalid username or password";

  private final UserRepository userRepository;
  private final HashingService hashingService;
  private final TokenService tokenService;
  private final RoleRepository roleRepository;

  /**
   * Compared against when the username does not exist, so an unknown user takes as long to reject
   * as a wrong password and the response time does not reveal which usernames exist.
   */
  private final String decoyHash;

  /**
   * Constructor for UserCommandServiceImpl.
   *
   * @param userRepository the user repository
   * @param hashingService the hashing service
   * @param tokenService the token service
   * @param roleRepository the role repository
   */
  public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService,
      TokenService tokenService, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.hashingService = hashingService;
    this.tokenService = tokenService;
    this.roleRepository = roleRepository;
    this.decoyHash = hashingService.encode(UUID.randomUUID().toString());
  }

  /**
   * Handle the sign-in command.
   *
   * <p>This method handles the {@link SignInCommand} command and returns the user and the token.
   * </p>
   *
   * @param command the sign-in command containing the username and password
   * @return and optional containing the user matching the username and the generated token
   * @throws BadCredentialsException with the same message whether the user is unknown or the
   *     password is wrong
   */
  @Override
  public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
    var user = userRepository.findByUsername(command.username());
    if (user.isEmpty()) {
      hashingService.matches(command.password(), decoyHash);
      throw new BadCredentialsException(INVALID_CREDENTIALS);
    }
    if (!hashingService.matches(command.password(), user.get().getPassword())) {
      throw new BadCredentialsException(INVALID_CREDENTIALS);
    }

    var token = tokenService.generateToken(user.get().getUsername());
    return Optional.of(ImmutablePair.of(user.get(), token));
  }

  /**
   * Handle the sign-up command.
   *
   * <p>This method handles the {@link SignUpCommand} command and returns the user.</p>
   *
   * @param command the sign-up command containing the username and password
   * @return the created user
   * @throws IllegalStateException if the username is taken
   */
  @Override
  public Optional<User> handle(SignUpCommand command) {
    if (userRepository.existsByUsername(command.username())) {
      throw new IllegalStateException("Username already exists");
    }
    var roles = command.roles().stream()
        .map(role ->
            roleRepository.findByName(role.getName())
                .orElseThrow(() -> new RuntimeException("Role name not found")))
        .toList();
    var user = new User(command.username(), hashingService.encode(command.password()), roles);
    userRepository.save(user);
    return userRepository.findByUsername(command.username());
  }

  /**
   * Handle the seed-administrator command.
   *
   * <p>Runs at startup, outside any HTTP request: the transaction keeps the roles attached while
   * the user is saved.</p>
   *
   * @param command the administrator's credentials
   * @return the created administrator, or empty if the username was already taken
   */
  @Override
  @Transactional
  public Optional<User> handle(SeedAdministratorCommand command) {
    if (userRepository.existsByUsername(command.username())) {
      return Optional.empty();
    }
    return handle(new SignUpCommand(
        command.username(), command.password(), List.of(new Role(Roles.ROLE_ADMIN))));
  }
}
