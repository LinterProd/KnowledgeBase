package com.metarash.backend.model.dto.response;

import com.metarash.backend.model.entity.DocumentStatus;

public record DocumentResponseDto(
        Long id,
        String title,
        String filePath,
        String fileType,
        Long fileSize,
        Long authorId,
        DocumentStatus status
) {}