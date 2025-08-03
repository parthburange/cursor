package com.financetracker.service;

import com.financetracker.dto.BudgetDto;
import com.financetracker.entity.Budget;
import com.financetracker.entity.Category;
import com.financetracker.entity.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public BudgetDto createBudget(BudgetDto budgetDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate date range
        if (budgetDto.getStartDate().isAfter(budgetDto.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        Budget budget = new Budget();
        budget.setBudgetAmount(budgetDto.getBudgetAmount());
        budget.setStartDate(budgetDto.getStartDate());
        budget.setEndDate(budgetDto.getEndDate());
        budget.setDescription(budgetDto.getDescription());
        budget.setIsActive(budgetDto.getIsActive());
        budget.setUser(user);

        // Set category if provided
        if (budgetDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(budgetDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Validate that category belongs to user
            if (!category.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Category does not belong to user");
            }

            budget.setCategory(category);
        }

        Budget savedBudget = budgetRepository.save(budget);
        log.info("Budget created: {} for user: {}", savedBudget.getId(), username);

        return convertToDto(savedBudget);
    }

    public BudgetDto updateBudget(Long budgetId, BudgetDto budgetDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Validate that budget belongs to user
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Budget does not belong to user");
        }

        // Validate date range
        if (budgetDto.getStartDate().isAfter(budgetDto.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        budget.setBudgetAmount(budgetDto.getBudgetAmount());
        budget.setStartDate(budgetDto.getStartDate());
        budget.setEndDate(budgetDto.getEndDate());
        budget.setDescription(budgetDto.getDescription());
        budget.setIsActive(budgetDto.getIsActive());

        // Update category if provided
        if (budgetDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(budgetDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Validate that category belongs to user
            if (!category.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Category does not belong to user");
            }

            budget.setCategory(category);
        } else {
            budget.setCategory(null);
        }

        Budget updatedBudget = budgetRepository.save(budget);
        log.info("Budget updated: {} for user: {}", updatedBudget.getId(), username);

        return convertToDto(updatedBudget);
    }

    public void deleteBudget(Long budgetId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Validate that budget belongs to user
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Budget does not belong to user");
        }

        budgetRepository.delete(budget);
        log.info("Budget deleted: {} for user: {}", budgetId, username);
    }

    public List<BudgetDto> getUserBudgets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return budgetRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BudgetDto> getActiveUserBudgets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return budgetRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BudgetDto> getActiveBudgetsByDate(String username, LocalDate date) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return budgetRepository.findActiveBudgetsByUserIdAndDate(user.getId(), date)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BudgetDto getBudgetById(Long budgetId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Validate that budget belongs to user
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Budget does not belong to user");
        }

        return convertToDto(budget);
    }

    private BudgetDto convertToDto(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setBudgetAmount(budget.getBudgetAmount());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        dto.setDescription(budget.getDescription());
        dto.setIsActive(budget.getIsActive());
        if (budget.getCategory() != null) {
            dto.setCategoryId(budget.getCategory().getId());
        }
        return dto;
    }
}