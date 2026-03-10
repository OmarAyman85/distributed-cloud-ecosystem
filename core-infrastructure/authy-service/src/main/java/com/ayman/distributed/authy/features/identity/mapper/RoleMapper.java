package com.ayman.distributed.authy.features.identity.mapper;

import com.ayman.distributed.authy.features.identity.dto.RoleRequestDTO;
import com.ayman.distributed.authy.features.identity.dto.RoleResponseDTO;
import com.ayman.distributed.authy.features.identity.model.Role;
import org.mapstruct.*;

/**
 * Mapper interface for converting between {@link Role} entities and DTOs.
 * Uses MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    /**
     * Converts a {@link Role} entity to its response DTO.
     *
     * @param role The role entity.
     * @return The response DTO.
     */
    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "application.appName", target = "applicationName")
    RoleResponseDTO toResponse(Role role);

    /**
     * Converts a request DTO to a {@link Role} entity.
     * Note: The application relationship must be set manually or via a separate service method,
     * as the DTO only contains the ID.
     *
     * @param dto The request DTO.
     * @return The role entity.
     */
    @Mapping(target = "application", ignore = true) // Handled in service
    Role toEntity(RoleRequestDTO dto);

    /**
     * Updates an existing {@link Role} entity from a request DTO.
     * Null values in the DTO are ignored.
     *
     * @param dto  The request DTO containing updates.
     * @param role The existing role entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "application", ignore = true) // Handled in service
    void updateRoleFromDto(RoleRequestDTO dto, @MappingTarget Role role);
}
