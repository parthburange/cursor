package com.financetracker.service;

import com.financetracker.entity.Category;
import com.financetracker.entity.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Category> getAllCategories() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return categoryRepository.findByUserId(user.getId());
    }
    
    public List<Category> getCategoriesByType(Category.CategoryType type) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return categoryRepository.findByUserIdAndType(user.getId(), type);
    }
    
    public Category createCategory(Category category) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        if (categoryRepository.existsByNameAndUserIdAndType(category.getName(), user.getId(), category.getType())) {
            throw new RuntimeException("Category with this name and type already exists");
        }
        
        category.setUser(user);
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Long id, Category categoryDetails) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to category");
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setType(categoryDetails.getType());
        
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to category");
        }
        
        categoryRepository.delete(category);
    }
}