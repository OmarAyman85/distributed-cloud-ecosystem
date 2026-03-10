package com.ayman.distributed.authy.features.identity.repository;

import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Application} entities.
 * Supports multi-tenant context by identifying applications by their unique names or keys.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Retrieves an application by its unique display name.
     * @param appName The name of the application.
     * @return An {@link Optional} containing the application if found.
     */
    Optional<Application> findByAppName(String appName);

    /**
     * Retrieves an application by its unique programmatic key.
     * @param appKey The key of the application (e.g., "KITCHEN_STORE").
     * @return An {@link Optional} containing the application if found.
     */
    Optional<Application> findByAppKey(String appKey);

    /**
     * Retrieves all applications with the given status.
     * @param status The status to filter by.
     * @return List of matching applications.
     */
    List<Application> findAllByStatus(ApplicationStatus status);
}