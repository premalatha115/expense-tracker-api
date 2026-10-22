package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(
        int month,
        int year,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategoryTotal> expenseByCategory,
        List<CategoryTotal> incomeByCategory) {
}
