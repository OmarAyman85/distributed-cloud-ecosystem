package com.ayman.distributed.authy.features.identity.service;

import com.ayman.distributed.authy.features.identity.dto.ApplicationRequestDTO;
import com.ayman.distributed.authy.features.identity.dto.ApplicationResponseDTO;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.Role;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.model.ApplicationStatus;
import com.ayman.distributed.authy.features.identity.repository.ApplicationRepository;
import com.ayman.distributed.authy.features.identity.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Service for managing applications and resolving app-scoped user roles.
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;

    // ─────────────────────────────────────────────────────────
    //  Lookup
    // ─────────────────────────────────────────────────────────

    /**
     * Finds an application by its unique appKey.
     *
     * @param appKey The application key (e.g., "KITCHEN_STORE").
     * @return The Application entity.
     * @throws EntityNotFoundException if no application exists with this key.
     */
    public Application findByAppKey(String appKey) {
        return applicationRepository.findByAppKey(appKey)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Application not found with key: " + appKey));
    }

    /**
     * Finds an application by appKey.
     * If appKey is null/blank, falls back to the default application.
     */
    public Application findByAppKeyOrDefault(String appKey) {
        if (appKey != null && !appKey.isBlank()) {
            return findByAppKey(appKey);
        }
        return findDefaultApp();
    }

    /**
     * Returns a default application.
     * Falls back to the first available application if no named default is found.
     */
    public Application findDefaultApp() {
        String defaultAppName = "Authy";
        return applicationRepository.findByAppName(defaultAppName)
                .orElseGet(() -> applicationRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(
                                "No application configured in the system.")));
    }

    // ─────────────────────────────────────────────────────────
    //  Role resolution
    // ─────────────────────────────────────────────────────────

    /**
     * Returns the role keys a user holds within a specific application.
     *
     * @param user   The user entity (with userRoles loaded).
     * @param appKey The application key.
     * @return List of role key strings (e.g., ["CUSTOMER", "ADMIN"]).
     */
    public List<String> getAppRolesForUser(User user, String appKey) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return Collections.emptyList();
        }
        return user.getUserRoles().stream()
                .filter(ur -> ur.getApplication() != null
                        && appKey.equals(ur.getApplication().getAppKey()))
                .map(ur -> ur.getRole().getRoleKey())
                .toList();
    }

    /**
     * Returns all active base URLs (for dynamic CORS).
     */
    public List<String> getActiveBaseUrls() {
        return applicationRepository.findAllByStatus(ApplicationStatus.ACTIVE).stream()
                .map(Application::getBaseUrl)
                .filter(url -> url != null && !url.isBlank())
                .toList();
    }

    // ─────────────────────────────────────────────────────────
    //  CRUD
    // ─────────────────────────────────────────────────────────

    /**
     * Lists all registered applications.
     */
    public List<ApplicationResponseDTO> listAll() {
        return applicationRepository.findAll().stream()
                .map(app -> new ApplicationResponseDTO(
                        app.getId(), app.getAppName(), app.getAppKey(),
                        app.getDescription(), app.getStatus()))
                .toList();
    }

    /**
     * Creates a new application with default ACTIVE status.
     */
    @Transactional
    public ApplicationResponseDTO create(ApplicationRequestDTO request) {
        Application app = Application.builder()
                .appKey(request.appKey())
                .appName(request.name())
                .description(request.description())
                .status(ApplicationStatus.ACTIVE)
                .build();
        applicationRepository.save(app);
        return new ApplicationResponseDTO(
                app.getId(), app.getAppName(), app.getAppKey(),
                app.getDescription(), app.getStatus());
    }

    /**
     * Adds a role to an existing application.
     *
     * @param appKey  The application key.
     * @param roleKey The role key (e.g., "CUSTOMER").
     * @param name    Human-readable role name.
     * @param description Optional description.
     */
    @Transactional
    public void addRoleToApp(String appKey, String roleKey, String name, String description) {
        Application app = findByAppKey(appKey);
        Role role = new Role();
        role.setRoleKey(roleKey);
        role.setName(name);
        role.setDescription(description);
        role.assignToApplication(app);
        roleRepository.save(role);
    }

    /**
     * Lists roles for a specific application.
     */
    public List<Role> getRolesForApp(String appKey) {
        Application app = findByAppKey(appKey);
        return roleRepository.findAllByApplication(app);
    }
}
