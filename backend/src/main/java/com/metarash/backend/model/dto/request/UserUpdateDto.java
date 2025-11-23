package com.metarash.backend.model.dto.request;

public record UserUpdateDto(
        String email,
        String password
) {}
