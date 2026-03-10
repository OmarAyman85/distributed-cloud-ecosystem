package com.ayman.distributed.authy.features.identity.repository;
import com.ayman.distributed.authy.features.identity.model.User;

import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Role} entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Finds a role by its unique name (e.g., "Standard User").
     * @param name The display name of the role.
     * @return An {@link Optional} containing the role if found.
     */
    Optional<Role> findByName(String name);

    /**
     * Finds all roles belonging to a specific application.
     */
    List<Role> findAllByApplication(Application application);

    /**
     * Finds a role by its application and role key.
     */
    Optional<Role> findByApplicationAndRoleKey(Application application, String roleKey);

    /**
     * Finds a role by its unique role key across all applications.
     */
    Optional<Role> findByRoleKey(String roleKey);
}
