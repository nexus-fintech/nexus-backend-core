package com.nexus.backend.iam.domain.services;

import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.domain.model.queries.GetAllRolesQuery;
import com.nexus.backend.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling role-related queries.
 */
public interface RoleQueryService {

  /**
   * Handle the GetAllRolesQuery and return a list of all roles.
   *
   * @param query The GetAllRolesQuery to process.
   * @return A list of all Role entities.
   */
  List<Role> handle(GetAllRolesQuery query);

  /**
   * Handle the GetRoleByNameQuery and return an Optional of Role.
   *
   * @param query The GetRoleByNameQuery containing the name of the role to retrieve.
   * @return An Optional containing the Role if found, or empty if not found.
   */
  Optional<Role> handle(GetRoleByNameQuery query);
}
