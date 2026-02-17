package com.metarash.backend.model.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponseDto user
) {}
