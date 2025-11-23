package com.metarash.backend.mapper;

import com.metarash.backend.model.dto.request.DocumentCreateDto;
import com.metarash.backend.model.dto.request.DocumentUpdateDto;
import com.metarash.backend.model.dto.response.DocumentResponseDto;
import com.metarash.backend.model.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    @Mapping(target = "category", ignore = true)  // We'll set manually
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "status", constant = "PUBLISHED")
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "fileType", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    Document toEntity(DocumentCreateDto dto);

    void updateFromDto(DocumentUpdateDto dto, @MappingTarget Document document);

    DocumentResponseDto toDto(Document document);
}