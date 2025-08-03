package com.financetracker.service;

import com.financetracker.entity.Category;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Transaction> getAllTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId());
    }
    
    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return transactionRepository.findByUserIdAndTypeOrderByTransactionDateDesc(user.getId(), type);
    }
    
    public List<Transaction> getTransactionsByCategory(Long categoryId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return transactionRepository.findByUserIdAndCategoryIdOrderByTransactionDateDesc(user.getId(), categoryId);
    }
    
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(user.getId(), startDate, endDate);
    }
    
    public Transaction createTransaction(Transaction transaction) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        transaction.setUser(user);
        transaction.setCategory(category);
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to transaction");
        }
        
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setType(transactionDetails.getType());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setNotes(transactionDetails.getNotes());
        
        if (transactionDetails.getCategory() != null) {
            Category category = categoryRepository.findById(transactionDetails.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            transaction.setCategory(category);
        }
        
        return transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to transaction");
        }
        
        transactionRepository.delete(transaction);
    }
    
    public Map<String, Object> getFinancialSummary(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                user.getId(), Transaction.TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                user.getId(), Transaction.TransactionType.EXPENSE, startDate, endDate);
        
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        
        BigDecimal netAmount = totalIncome.subtract(totalExpense);
        
        List<Object[]> categoryExpenses = transactionRepository.getCategoryTotalsByUserIdAndTypeAndDateBetween(
                user.getId(), Transaction.TransactionType.EXPENSE, startDate, endDate);
        
        Map<String, BigDecimal> expenseByCategory = categoryExpenses.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));
        
        return Map.of(
                "totalIncome", totalIncome,
                "totalExpense", totalExpense,
                "netAmount", netAmount,
                "expenseByCategory", expenseByCategory
        );
    }
}