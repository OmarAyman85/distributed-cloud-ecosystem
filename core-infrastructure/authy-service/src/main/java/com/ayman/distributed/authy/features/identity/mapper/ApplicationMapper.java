package com.ayman.distributed.authy.features.identity.mapper;

import com.ayman.distributed.authy.features.identity.dto.ApplicationRequestDTO;
import com.ayman.distributed.authy.features.identity.dto.ApplicationResponseDTO;
import com.ayman.distributed.authy.features.identity.model.Application;
import org.mapstruct.*;

/**
 * Mapper interface for converting between {@link Application} entities and DTOs.
 * Uses MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationMapper {

    /**
     * Converts an {@link Application} entity to its response DTO.
     *
     * @param application The application entity.
     * @return The response DTO.
     */
    ApplicationResponseDTO toResponse(Application application);

    /**
     * Converts a request DTO to an {@link Application} entity.
     *
     * @param dto The request DTO.
     * @return The application entity.
     */
    Application toEntity(ApplicationRequestDTO dto);

    /**
     * Updates an existing {@link Application} entity from a request DTO.
     * Null values in the DTO are ignored.
     *
     * @param dto         The request DTO containing updates.
     * @param application The existing application entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateApplicationFromDto(ApplicationRequestDTO dto, @MappingTarget Application application);
}
