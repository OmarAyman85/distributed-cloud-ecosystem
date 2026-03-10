package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for creating or updating a role.
 */
@Schema(description = "Role creation/update request")
public record RoleRequestDTO(
        @NotBlank 
        @Schema(description = "Display name of the role", example = "Administrator")
        String name,

        @NotBlank 
        @Schema(description = "Internal unique key for the role", example = "ROLE_ADMIN")
        String roleKey,

        @Schema(description = "Detailed description of the role", example = "Has full access to all system features")
        String description,

        @Schema(description = "ID of the application this role belongs to")
        Long applicationId
) {
}
