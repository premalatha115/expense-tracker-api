package com.expensetracker.dto;

import com.expensetracker.entity.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull(message = "Type is required (INCOME or EXPENSE)")
        TransactionType type,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer and 2 decimal digits")
        BigDecimal amount,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @NotNull(message = "Date is required (yyyy-MM-dd)")
        LocalDate date,

        @NotNull(message = "Category id is required")
        Long categoryId) {
}
