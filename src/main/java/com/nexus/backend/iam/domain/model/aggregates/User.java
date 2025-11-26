package com.nexus.backend.iam.domain.model.aggregates;

import com.nexus.backend.iam.domain.model.entities.Role;
import com.nexus.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * User aggregate root
 * This class represents the aggregate root for the User entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    /**
     * Default constructor for JPA.
     */
    public User() {
        this.roles = new HashSet<>();
    }

    /**
     * Constructs a User with the specified username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
    }

    /**
     * Constructs a User with the specified username, password, and roles.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param roles    the list of roles assigned to the user
     */
    public User(String username, String password, List<Role> roles) {
        this(username, password);
        addRoles(roles);
    }

    /**
     * Add a role to the user.
     *
     * @param role the role to add
     * @return the user with the added role
     */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    /**
     * Add a list of roles to the user.
     *
     * @param roles the list of roles to add
     * @return the user with the added roles
     */
    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }
}