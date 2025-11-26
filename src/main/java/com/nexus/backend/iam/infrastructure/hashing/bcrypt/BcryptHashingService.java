package com.nexus.backend.iam.infrastructure.hashing.bcrypt;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.nexus.backend.iam.application.internal.outboundservices.hashing.HashingService;
import com.nexus.backend.iam.infrastructure.hashing.bcrypt.services.HashingServiceImpl;


/**
 * This interface is a marker interface for the BCrypt hashing service.
 * It extends the {@link HashingService} and {@link PasswordEncoder} interfaces.
 * This interface is used to inject the BCrypt hashing service in the
 * {@link HashingServiceImpl} class.
 */
public interface BcryptHashingService extends HashingService, PasswordEncoder {
}
