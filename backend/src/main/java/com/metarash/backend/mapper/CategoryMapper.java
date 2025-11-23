package com.metarash.backend.mapper;

import com.metarash.backend.model.dto.request.CategoryCreateDto;
import com.metarash.backend.model.dto.request.CategoryUpdateDto;
import com.metarash.backend.model.dto.response.CategoryResponseDto;
import com.metarash.backend.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryCreateDto dto);
    Category toEntity(CategoryUpdateDto dto);
    void updateFromDto(CategoryUpdateDto dto, @MappingTarget Category category);
    CategoryResponseDto toDto(Category category);  // Assuming you have response DTOs
}