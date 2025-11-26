package com.nexus.backend.iam.interfaces.rest.transform;

import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.interfaces.rest.resources.RoleResource;

/**
 * Assembler class to convert Role entities to RoleResource DTOs.
 */
public class RoleResourceFromEntityAssembler {

  /**
   * Converts a Role entity to a RoleResource DTO.
   *
   * @param role the Role entity to convert
   * @return the corresponding RoleResource DTO
   */
  public static RoleResource toResourceFromEntity(Role role) {
    return new RoleResource(role.getId(), role.getStringName());
  }
}
