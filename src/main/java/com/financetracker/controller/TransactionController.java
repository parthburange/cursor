package com.financetracker.controller;

import com.financetracker.dto.TransactionDto;
import com.financetracker.entity.Transaction;
import com.financetracker.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction management APIs")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Creates a new financial transaction")
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto, 
                                                           Authentication authentication) {
        try {
            TransactionDto created = transactionService.createTransaction(transactionDto, authentication.getName());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create transaction: {}", e.getMessage());
            throw new RuntimeException("Failed to create transaction: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a transaction", description = "Updates an existing transaction")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, 
                                                           @Valid @RequestBody TransactionDto transactionDto,
                                                           Authentication authentication) {
        try {
            TransactionDto updated = transactionService.updateTransaction(id, transactionDto, authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to update transaction: {}", e.getMessage());
            throw new RuntimeException("Failed to update transaction: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction", description = "Deletes a transaction")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, Authentication authentication) {
        try {
            transactionService.deleteTransaction(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete transaction: {}", e.getMessage());
            throw new RuntimeException("Failed to delete transaction: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieves all transactions for the authenticated user")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(Authentication authentication) {
        try {
            List<TransactionDto> transactions = transactionService.getUserTransactions(authentication.getName());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Failed to get transactions: {}", e.getMessage());
            throw new RuntimeException("Failed to get transactions: " + e.getMessage());
        }
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get transactions by type", description = "Retrieves transactions filtered by type (INCOME/EXPENSE)")
    public ResponseEntity<List<TransactionDto>> getTransactionsByType(@PathVariable Transaction.TransactionType type,
                                                                     Authentication authentication) {
        try {
            List<TransactionDto> transactions = transactionService.getUserTransactionsByType(authentication.getName(), type);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Failed to get transactions by type: {}", e.getMessage());
            throw new RuntimeException("Failed to get transactions by type: " + e.getMessage());
        }
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range", description = "Retrieves transactions within a date range")
    public ResponseEntity<List<TransactionDto>> getTransactionsByDateRange(@RequestParam LocalDate startDate,
                                                                          @RequestParam LocalDate endDate,
                                                                          Authentication authentication) {
        try {
            List<TransactionDto> transactions = transactionService.getUserTransactionsByDateRange(
                    authentication.getName(), startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Failed to get transactions by date range: {}", e.getMessage());
            throw new RuntimeException("Failed to get transactions by date range: " + e.getMessage());
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get financial summary", description = "Retrieves income and expense summary for a date range")
    public ResponseEntity<FinancialSummary> getFinancialSummary(@RequestParam LocalDate startDate,
                                                               @RequestParam LocalDate endDate,
                                                               Authentication authentication) {
        try {
            BigDecimal totalIncome = transactionService.getTotalIncome(authentication.getName(), startDate, endDate);
            BigDecimal totalExpenses = transactionService.getTotalExpenses(authentication.getName(), startDate, endDate);
            BigDecimal netAmount = totalIncome.subtract(totalExpenses);

            FinancialSummary summary = new FinancialSummary(totalIncome, totalExpenses, netAmount);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Failed to get financial summary: {}", e.getMessage());
            throw new RuntimeException("Failed to get financial summary: " + e.getMessage());
        }
    }

    public static class FinancialSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netAmount;

        public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netAmount) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netAmount = netAmount;
        }

        // Getters and setters
        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
        public BigDecimal getTotalExpenses() { return totalExpenses; }
        public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
        public BigDecimal getNetAmount() { return netAmount; }
        public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    }
}