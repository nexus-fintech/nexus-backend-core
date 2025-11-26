package com.nexus.backend.iam.domain.model.queries;

import com.nexus.backend.iam.domain.model.valueobjects.Roles;

/**
 * Query to get a role by its name.
 *
 * @param name the name of the role
 */
public record GetRoleByNameQuery(Roles name) {
}
