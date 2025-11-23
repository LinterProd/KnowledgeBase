package com.metarash.backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentCreateDto(
        @NotBlank @Size(max = 500) String title,
        String description,
        Long categoryId
) {}
