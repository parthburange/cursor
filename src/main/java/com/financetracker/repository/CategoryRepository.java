package com.financetracker.repository;

import com.financetracker.entity.Category;
import com.financetracker.entity.Category.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);
    
    List<Category> findByUserIdAndType(Long userId, CategoryType type);
    
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.name = :name")
    Optional<Category> findByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
    
    boolean existsByUserIdAndName(Long userId, String name);
}