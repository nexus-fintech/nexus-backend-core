package com.nexus.backend.iam.interfaces.rest.transform;

import com.nexus.backend.iam.domain.model.commands.SignUpCommand;
import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.interfaces.rest.resources.SignUpResource;

import java.util.List;

/**
 * Assembler class to convert SignUpResource to SignUpCommand.
 */
public class SignUpCommandFromResourceAssembler {

  /**
   * Converts a SignUpResource to a SignUpCommand.
   * The role is always the default one (client): administrators are never created through the
   * public sign-up.
   *
   * @param resource the SignUpResource to convert
   * @return the corresponding SignUpCommand
   */
  public static SignUpCommand toCommandFromResource(SignUpResource resource) {
    return new SignUpCommand(resource.username(), resource.password(), List.of(Role.getDefaultRole()));
  }
}
