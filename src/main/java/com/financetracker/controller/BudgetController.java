package com.financetracker.controller;

import com.financetracker.dto.BudgetDto;
import com.financetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Budgets", description = "Budget management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a new budget", description = "Creates a new spending budget")
    public ResponseEntity<BudgetDto> createBudget(@Valid @RequestBody BudgetDto budgetDto, 
                                                 Authentication authentication) {
        try {
            BudgetDto created = budgetService.createBudget(budgetDto, authentication.getName());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create budget: {}", e.getMessage());
            throw new RuntimeException("Failed to create budget: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a budget", description = "Updates an existing budget")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable Long id, 
                                                 @Valid @RequestBody BudgetDto budgetDto,
                                                 Authentication authentication) {
        try {
            BudgetDto updated = budgetService.updateBudget(id, budgetDto, authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to update budget: {}", e.getMessage());
            throw new RuntimeException("Failed to update budget: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget", description = "Deletes a budget")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id, Authentication authentication) {
        try {
            budgetService.deleteBudget(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete budget: {}", e.getMessage());
            throw new RuntimeException("Failed to delete budget: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all budgets", description = "Retrieves all budgets for the authenticated user")
    public ResponseEntity<List<BudgetDto>> getAllBudgets(Authentication authentication) {
        try {
            List<BudgetDto> budgets = budgetService.getUserBudgets(authentication.getName());
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            log.error("Failed to get budgets: {}", e.getMessage());
            throw new RuntimeException("Failed to get budgets: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get active budgets", description = "Retrieves only active budgets for the authenticated user")
    public ResponseEntity<List<BudgetDto>> getActiveBudgets(Authentication authentication) {
        try {
            List<BudgetDto> budgets = budgetService.getActiveUserBudgets(authentication.getName());
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            log.error("Failed to get active budgets: {}", e.getMessage());
            throw new RuntimeException("Failed to get active budgets: " + e.getMessage());
        }
    }

    @GetMapping("/active-by-date")
    @Operation(summary = "Get active budgets by date", description = "Retrieves active budgets for a specific date")
    public ResponseEntity<List<BudgetDto>> getActiveBudgetsByDate(@RequestParam LocalDate date,
                                                                 Authentication authentication) {
        try {
            List<BudgetDto> budgets = budgetService.getActiveBudgetsByDate(authentication.getName(), date);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            log.error("Failed to get active budgets by date: {}", e.getMessage());
            throw new RuntimeException("Failed to get active budgets by date: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Retrieves a specific budget by ID")
    public ResponseEntity<BudgetDto> getBudgetById(@PathVariable Long id, Authentication authentication) {
        try {
            BudgetDto budget = budgetService.getBudgetById(id, authentication.getName());
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            log.error("Failed to get budget: {}", e.getMessage());
            throw new RuntimeException("Failed to get budget: " + e.getMessage());
        }
    }
}