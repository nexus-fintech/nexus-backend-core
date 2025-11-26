package com.nexus.backend.iam.interfaces.rest.transform;

import com.nexus.backend.iam.domain.model.aggregates.User;
import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.interfaces.rest.resources.UserResource;

/**
 * Assembler class to transform User entities into UserResource DTOs.
 */
public class UserResourceFromEntityAssembler {

  /** Transforms a User entity into a UserResource DTO.
   *
   * @param user the User entity to be transformed
   * @return a UserResource DTO containing the user's details
   */
  public static UserResource toResourceFromEntity(User user) {
    var roles = user.getRoles().stream()
        .map(Role::getStringName)
        .toList();
    return new UserResource(user.getId(), user.getUsername(), roles);
  }
}
