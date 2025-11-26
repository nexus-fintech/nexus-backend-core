package com.nexus.backend.iam.application.internal.queryservices;

import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.iam.domain.model.queries.GetAllRolesQuery;
import com.nexus.backend.iam.domain.model.queries.GetRoleByNameQuery;
import com.nexus.backend.iam.domain.services.RoleQueryService;
import com.nexus.backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * RoleQueryServiceImpl.
 *
 * <p>This class implements the RoleQueryService interface and provides the implementation for
 *     handling role-related queries.
 * </p>
 */
@Service
public class RoleQueryServiceImpl implements RoleQueryService {
  private final RoleRepository roleRepository;

  /**
   * Constructor for RoleQueryServiceImpl.
   *
   * @param roleRepository the role repository
   */
  public RoleQueryServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /**
   * Handle the get all roles query.
   *
   * @param query the get all roles query
   * @return List of Role the list of roles
   */
  @Override
  public List<Role> handle(GetAllRolesQuery query) {
    return roleRepository.findAll();
  }

  /**
   * Handle the get role by name query.
   *
   * @param query the get role by name query
   * @return Optional of Role the role if found
   */
  @Override
  public Optional<Role> handle(GetRoleByNameQuery query) {
    return roleRepository.findByName(query.name());
  }
}
