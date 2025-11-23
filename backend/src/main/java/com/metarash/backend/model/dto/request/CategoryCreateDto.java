package com.metarash.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateDto(
        @NotBlank String name,
        String description
) {}
