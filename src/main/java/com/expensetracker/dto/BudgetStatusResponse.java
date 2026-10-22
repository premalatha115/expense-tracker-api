package com.expensetracker.dto;

import java.math.BigDecimal;

public record BudgetStatusResponse(
        Long budgetId,
        String category,
        Integer month,
        Integer year,
        BigDecimal limitAmount,
        BigDecimal spent,
        BigDecimal remaining,
        BigDecimal percentUsed,
        String status) {
}
