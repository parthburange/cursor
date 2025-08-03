package com.financetracker.repository;

import com.financetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);
    
    List<Budget> findByUserIdAndIsActiveTrue(Long userId);
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.isActive = true AND :date BETWEEN b.startDate AND b.endDate")
    List<Budget> findActiveBudgetsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.category.id = :categoryId AND b.isActive = true AND :date BETWEEN b.startDate AND b.endDate")
    Optional<Budget> findActiveBudgetByUserIdAndCategoryAndDate(
            @Param("userId") Long userId, 
            @Param("categoryId") Long categoryId, 
            @Param("date") LocalDate date);
    
    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
}