package com.financetracker.repository;

import com.financetracker.entity.Category;
import com.financetracker.entity.Category.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdAndType(Long userId, CategoryType type);
    List<Category> findByUserId(Long userId);
    boolean existsByNameAndUserIdAndType(String name, Long userId, CategoryType type);
}