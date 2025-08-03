package com.financetracker.service;

import com.financetracker.dto.CategoryDto;
import com.financetracker.entity.Category;
import com.financetracker.entity.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryDto createCategory(CategoryDto categoryDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if category name already exists for this user
        if (categoryRepository.existsByUserIdAndName(user.getId(), categoryDto.getName())) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setType(categoryDto.getType());
        category.setColorHex(categoryDto.getColorHex());
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {} for user: {}", savedCategory.getId(), username);

        return convertToDto(savedCategory);
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate that category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to user");
        }

        // Check if new name conflicts with existing category (excluding current category)
        if (!category.getName().equals(categoryDto.getName()) && 
            categoryRepository.existsByUserIdAndName(user.getId(), categoryDto.getName())) {
            throw new RuntimeException("Category with this name already exists");
        }

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setType(categoryDto.getType());
        category.setColorHex(categoryDto.getColorHex());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {} for user: {}", updatedCategory.getId(), username);

        return convertToDto(updatedCategory);
    }

    public void deleteCategory(Long categoryId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate that category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to user");
        }

        // Check if category has transactions
        if (!category.getTransactions().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing transactions");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {} for user: {}", categoryId, username);
    }

    public List<CategoryDto> getUserCategories(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getUserCategoriesByType(String username, Category.CategoryType type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findByUserIdAndType(user.getId(), type)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long categoryId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate that category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to user");
        }

        return convertToDto(category);
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setType(category.getType());
        dto.setColorHex(category.getColorHex());
        return dto;
    }
}