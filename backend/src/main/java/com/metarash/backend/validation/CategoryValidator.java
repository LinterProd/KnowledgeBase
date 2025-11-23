package com.metarash.backend.validation;

import com.metarash.backend.model.dto.request.CategoryCreateDto;
import com.metarash.backend.model.dto.request.CategoryUpdateDto;
import com.metarash.backend.repository.CategoryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateCreate(CategoryCreateDto dto) {
        if (dto == null) {
            throw new ValidationException("DTO is required");
        }
        if (StringUtils.isBlank(dto.name())) {
            throw new ValidationException("Category name is required");
        }
        if (categoryRepository.existsByName(dto.name())) {
            throw new ValidationException("Category name already exists: " + dto.name());
        }
        // Additional: length checks if not in DTO
    }

    public void validateUpdate(CategoryUpdateDto dto, Long id) {
        if (dto == null) {
            throw new ValidationException("DTO is required");
        }
        if (dto.name() != null) {
            categoryRepository.findByName(dto.name())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new ValidationException("Category name already exists: " + dto.name());
                        }
                    });
        }
    }
}