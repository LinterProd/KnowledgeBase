package com.metarash.backend.controller;

import com.metarash.backend.model.dto.request.CategoryCreateDto;
import com.metarash.backend.model.dto.request.CategoryUpdateDto;
import com.metarash.backend.model.dto.response.CategoryResponseDto;
import com.metarash.backend.model.entity.Category;
import com.metarash.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryCreateDto dto) {
        Category category = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories.stream().map(this::mapToDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(mapToDto(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryUpdateDto dto) {
        Category updated = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Маппер
    private CategoryResponseDto mapToDto(Category category) {
        return new CategoryResponseDto(category.getId(), category.getName(), category.getDescription());
    }
}