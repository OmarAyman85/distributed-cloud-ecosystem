package com.ayman.distributed.authy.features.identity.mapper;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;

import com.ayman.distributed.authy.features.identity.dto.*;
import com.ayman.distributed.authy.features.identity.model.Address;
import com.ayman.distributed.authy.features.identity.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /* -------------------- Entity to DTO -------------------- */

    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponseDTO toResponse(User user);

    /* -------------------- DTO to Entity (registration) -------------------- */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "authProvider", constant = "LOCAL")
    User fromStandardRegistration(StandardUserRegistrationRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true) // OAuth users don't have passwords
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "emailVerified", constant = "true")
    User fromOAuthRegistration(OAuthUserRegistrationRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "emailVerified", constant = "true") // Phone usually verified immediately
    @Mapping(target = "authProvider", constant = "PHONE")
    User fromPhoneRegistration(PhoneUserRegistrationRequestDTO dto);

    /* -------------------- Update existing entity -------------------- */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateRequestDTO dto, @MappingTarget User user);

    /* -------------------- Address Mapping -------------------- */

    AddressDTO toAddressDTO(Address address);

    Address toAddress(AddressDTO dto);
}
