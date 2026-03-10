package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Data Transfer Object for returning role details.
 */
@Schema(description = "Role information response")
public record RoleResponseDTO(
        @Schema(description = "Unique identifier of the role", example = "1")
        Long id,

        @Schema(description = "Display name of the role", example = "Administrator")
        String name,

        @Schema(description = "Internal unique key for the role", example = "ROLE_ADMIN")
        String roleKey,

        @Schema(description = "Detailed description of the role", example = "Has full access to all system features")
        String description,

        @Schema(description = "ID of the application this role belongs to")
        Long applicationId,

        @Schema(description = "Name of the application this role belongs to", example = "Authy Master")
        String applicationName
) {
}
