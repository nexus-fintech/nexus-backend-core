package com.nexus.backend.iam.interfaces.rest.transform;

import com.nexus.backend.iam.domain.model.commands.SignInCommand;
import com.nexus.backend.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler to transform SignInResource into SignInCommand.
 */
public class SignInCommandFromResourceAssembler {

  /**
   * Transform a SignInResource into a SignInCommand.
   *
   * @param signInResource the resource to be transformed
   * @return the command
   */
  public static SignInCommand toCommandFromResource(SignInResource signInResource) {
    return new SignInCommand(signInResource.username(), signInResource.password());
  }
}
