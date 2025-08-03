package com.financetracker.controller;

import com.financetracker.dto.CategoryDto;
import com.financetracker.entity.Category;
import com.financetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Category management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new transaction category")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto, 
                                                     Authentication authentication) {
        try {
            CategoryDto created = categoryService.createCategory(categoryDto, authentication.getName());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create category: {}", e.getMessage());
            throw new RuntimeException("Failed to create category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates an existing category")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, 
                                                     @Valid @RequestBody CategoryDto categoryDto,
                                                     Authentication authentication) {
        try {
            CategoryDto updated = categoryService.updateCategory(id, categoryDto, authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to update category: {}", e.getMessage());
            throw new RuntimeException("Failed to update category: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category (only if no transactions exist)")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, Authentication authentication) {
        try {
            categoryService.deleteCategory(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete category: {}", e.getMessage());
            throw new RuntimeException("Failed to delete category: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves all categories for the authenticated user")
    public ResponseEntity<List<CategoryDto>> getAllCategories(Authentication authentication) {
        try {
            List<CategoryDto> categories = categoryService.getUserCategories(authentication.getName());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Failed to get categories: {}", e.getMessage());
            throw new RuntimeException("Failed to get categories: " + e.getMessage());
        }
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get categories by type", description = "Retrieves categories filtered by type (INCOME/EXPENSE)")
    public ResponseEntity<List<CategoryDto>> getCategoriesByType(@PathVariable Category.CategoryType type,
                                                                Authentication authentication) {
        try {
            List<CategoryDto> categories = categoryService.getUserCategoriesByType(authentication.getName(), type);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Failed to get categories by type: {}", e.getMessage());
            throw new RuntimeException("Failed to get categories by type: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a specific category by ID")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id, Authentication authentication) {
        try {
            CategoryDto category = categoryService.getCategoryById(id, authentication.getName());
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            log.error("Failed to get category: {}", e.getMessage());
            throw new RuntimeException("Failed to get category: " + e.getMessage());
        }
    }
}