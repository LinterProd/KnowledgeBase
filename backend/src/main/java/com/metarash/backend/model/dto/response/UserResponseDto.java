package com.metarash.backend.model.dto.response;

import com.metarash.backend.model.entity.User;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        User.UserRole role,
        String status
) {}