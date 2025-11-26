package com.nexus.backend.iam.infrastructure.authorization.sfs.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.nexus.backend.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.nexus.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;

/**
 * This class is responsible for providing the user details to the Spring Security framework.
 * It implements the UserDetailsService interface.
 */
@Service(value = "defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Constructor with dependencies.
   *
   * @param userRepository The user repository.
   */
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * This method is responsible for loading the user details from the database.
   *
   * @param username The username.
   * @return The UserDetails object.
   * @throws UsernameNotFoundException If the user is not found.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));
    return UserDetailsImpl.build(user);
  }
}
