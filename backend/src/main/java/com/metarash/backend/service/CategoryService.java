package com.metarash.backend.service;

import com.metarash.backend.exception.EntityNotFoundException;
import com.metarash.backend.mapper.CategoryMapper;
import com.metarash.backend.model.dto.request.CategoryCreateDto;
import com.metarash.backend.model.dto.request.CategoryUpdateDto;
import com.metarash.backend.model.entity.Category;
import com.metarash.backend.repository.CategoryRepository;
import com.metarash.backend.validation.CategoryValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    @Transactional
    public Category createCategory(CategoryCreateDto dto) {
        categoryValidator.validateCreate(dto);
        Category category = categoryMapper.toEntity(dto);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
    }

    @Transactional
    public Category updateCategory(Long id, CategoryUpdateDto dto) {
        Category category = getCategoryById(id);
        categoryValidator.validateUpdate(dto, id);
        categoryMapper.updateFromDto(dto, category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}

