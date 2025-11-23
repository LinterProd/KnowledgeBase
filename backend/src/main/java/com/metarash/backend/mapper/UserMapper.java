package com.metarash.backend.mapper;

import com.metarash.backend.model.dto.request.UserCreateDto;
import com.metarash.backend.model.dto.request.UserUpdateDto;
import com.metarash.backend.model.dto.response.UserResponseDto;
import com.metarash.backend.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserCreateDto dto);

    void updateFromDto(UserUpdateDto dto, @MappingTarget User user);

    UserResponseDto toDto(User user);
}