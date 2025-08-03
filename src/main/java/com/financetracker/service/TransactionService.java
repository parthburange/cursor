package com.financetracker.service;

import com.financetracker.dto.TransactionDto;
import com.financetracker.entity.Category;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
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
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionDto createTransaction(TransactionDto transactionDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate that category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to user");
        }

        Transaction transaction = new Transaction();
        transaction.setDescription(transactionDto.getDescription());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        transaction.setType(transactionDto.getType());
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setPaymentMethod(transactionDto.getPaymentMethod());
        transaction.setNotes(transactionDto.getNotes());
        transaction.setIsRecurring(transactionDto.getIsRecurring());
        transaction.setRecurrenceType(transactionDto.getRecurrenceType());

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created: {} for user: {}", savedTransaction.getId(), username);

        return convertToDto(savedTransaction);
    }

    public TransactionDto updateTransaction(Long transactionId, TransactionDto transactionDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Validate that transaction belongs to user
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Transaction does not belong to user");
        }

        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Validate that category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category does not belong to user");
        }

        transaction.setDescription(transactionDto.getDescription());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        transaction.setType(transactionDto.getType());
        transaction.setCategory(category);
        transaction.setPaymentMethod(transactionDto.getPaymentMethod());
        transaction.setNotes(transactionDto.getNotes());
        transaction.setIsRecurring(transactionDto.getIsRecurring());
        transaction.setRecurrenceType(transactionDto.getRecurrenceType());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction updated: {} for user: {}", updatedTransaction.getId(), username);

        return convertToDto(updatedTransaction);
    }

    public void deleteTransaction(Long transactionId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Validate that transaction belongs to user
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Transaction does not belong to user");
        }

        transactionRepository.delete(transaction);
        log.info("Transaction deleted: {} for user: {}", transactionId, username);
    }

    public List<TransactionDto> getUserTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getUserTransactionsByType(String username, Transaction.TransactionType type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserIdAndTypeOrderByTransactionDateDesc(user.getId(), type)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getUserTransactionsByDateRange(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(user.getId(), startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalIncome(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(user.getId(), Transaction.TransactionType.INCOME, startDate, endDate);
    }

    public BigDecimal getTotalExpenses(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(user.getId(), Transaction.TransactionType.EXPENSE, startDate, endDate);
    }

    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setType(transaction.getType());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setNotes(transaction.getNotes());
        dto.setIsRecurring(transaction.getIsRecurring());
        dto.setRecurrenceType(transaction.getRecurrenceType());
        return dto;
    }
}