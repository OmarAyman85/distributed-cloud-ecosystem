package com.ayman.distributed.authy.features.identity.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRoleRequestDTO(
        @NotNull Long userId,
        @NotNull Long roleId,
        @NotNull Long applicationId
) {
}
