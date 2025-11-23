package com.metarash.backend.model.dto.response;

import com.metarash.backend.model.entity.DocumentStatus;

public record DocumentResponseDto(
        Long id,
        String title,
        String description,
        String filePath,
        String fileType,
        Long fileSize,
        Long categoryId,
        Long authorId,
        DocumentStatus status
) {}