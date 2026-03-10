package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.Application;

import com.ayman.distributed.authy.features.identity.model.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for returning application details.
 */
@Schema(description = "Application information response")
public record ApplicationResponseDTO(
        @Schema(description = "Unique identifier of the application", example = "1")
        Long id,

        @Schema(description = "Display name of the application", example = "E-Commerce Web")
        String name,

        @Schema(description = "Internal unique key for the application", example = "WEB_APP")
        String appKey,

        @Schema(description = "Detailed description of the application", example = "Main customer facing web application")
        String description,

        @Schema(description = "Current operational status of the application")
        ApplicationStatus status
) {}
