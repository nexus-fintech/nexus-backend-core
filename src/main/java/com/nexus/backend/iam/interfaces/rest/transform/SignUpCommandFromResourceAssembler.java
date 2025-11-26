package com.nexus.backend.iam.interfaces.rest.transform;

import com.nexus.backend.iam.domain.model.commands.SignUpCommand;
import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

/**
 * Assembler class to convert SignUpResource to SignUpCommand.
 */
public class SignUpCommandFromResourceAssembler {

  /**
   * Converts a SignUpResource to a SignUpCommand.
   *
   * @param resource the SignUpResource to convert
   * @return the corresponding SignUpCommand
   */
  public static SignUpCommand toCommandFromResource(SignUpResource resource) {
    var roles = resource.roles() != null
        ? resource.roles().stream().map(name -> Role.toRoleFromName(name)).toList()
        : new ArrayList<Role>();
    return new SignUpCommand(resource.username(), resource.password(), roles);
  }
}
