package com.metarash.backend.model.dto.request;

public record DocumentUpdateDto(
        String title,
        String description,
        Long categoryId
) {}
