package com.expensetracker.dto;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        Integer month,
        Integer year,
        BigDecimal limitAmount) {
}
