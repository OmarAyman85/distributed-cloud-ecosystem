package com.ayman.distributed.authy.features.identity.repository;

import com.ayman.distributed.authy.features.identity.model.Role;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link UserRole} assignments.
 * This handles the many-to-many relationship between users, roles, and applications.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Retrieves all role assignments for a specific user.
     * @param user The user.
     * @return List of user-role assignments.
     */
    List<UserRole> findByUser(User user);

    /**
     * Finds a specific role assignment for a user.
     * @param user The user.
     * @param role The role.
     * @return An {@link Optional} containing the assignment if found.
     */
    Optional<UserRole> findByUserAndRole(User user, Role role);
}