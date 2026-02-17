package com.metarash.backend.mapper;

import com.metarash.backend.model.dto.request.DocumentUpdateDto;
import com.metarash.backend.model.dto.response.DocumentResponseDto;
import com.metarash.backend.model.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    void updateFromDto(DocumentUpdateDto dto, @MappingTarget Document document);

    @Mapping(target = "authorId", source = "author.id")
    DocumentResponseDto toDto(Document document);
}