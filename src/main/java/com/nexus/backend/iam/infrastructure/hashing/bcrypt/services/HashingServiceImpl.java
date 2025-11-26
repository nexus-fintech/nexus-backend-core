package com.nexus.backend.iam.infrastructure.hashing.bcrypt.services;

import com.nexus.backend.iam.infrastructure.hashing.bcrypt.BcryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This class implements the {@link BcryptHashingService} interface.
 * It is used to hash passwords using the BCrypt algorithm.
 */
@Service
public class HashingServiceImpl implements BcryptHashingService {
  private final BCryptPasswordEncoder passwordEncoder;

  HashingServiceImpl() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  /**
   * Hash a password using the BCrypt algorithm.
   *
   * @param rawPassword the password to hash
   * @return String the hashed password
   */
  @Override
  public String encode(CharSequence rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  /**
   * Check if a raw password matches a hashed password.
   *
   * @param rawPassword the raw password
   * @param encodedPassword the hashed password
   * @return boolean true if the raw password matches the hashed password, false otherwise
   */
  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
