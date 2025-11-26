package com.nexus.backend.iam.interfaces.rest.resources;

/**
 * Resource representation of a Role in the IAM system.
 *
 * @param id   the unique identifier of the role
 * @param name the name of the role
 */
public record RoleResource(Long id, String name) {
}
