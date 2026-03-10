package com.ayman.distributed.authy.features.identity.dto;

import java.time.Instant;

public record UserRoleResponseDTO(
        Long id,
        Long userId,
        String username,
        Long roleId,
        String roleName,
        Long applicationId,
        String applicationName,
        Instant assignedAt
) {
}
