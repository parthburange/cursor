package com.financetracker.dto;

import com.financetracker.entity.Transaction.TransactionType;
import com.financetracker.entity.Transaction.RecurrenceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {

    private Long id;

    @NotBlank(message = "Transaction description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String paymentMethod;
    private String notes;
    private Boolean isRecurring = false;
    private RecurrenceType recurrenceType;
}