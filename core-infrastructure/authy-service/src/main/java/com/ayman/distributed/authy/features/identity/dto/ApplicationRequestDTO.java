package com.ayman.distributed.authy.features.identity.dto;
import com.ayman.distributed.authy.features.identity.model.Application;

import com.ayman.distributed.authy.features.identity.model.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating or updating an application.
 */
@Schema(description = "Application creation/update request")
public record ApplicationRequestDTO(
        @NotBlank 
        @Schema(description = "Display name of the application", example = "E-Commerce Web")
        String name,

        @NotBlank 
        @Schema(description = "Internal unique key for the application", example = "WEB_APP")
        String appKey,

        @Schema(description = "Detailed description of the application's purpose", example = "Main customer facing web application")
        String description
) {
}
