package com.expensetracker.dto;

import com.expensetracker.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate date,
        Long categoryId,
        String categoryName) {
}
