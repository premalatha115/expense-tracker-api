package com.expensetracker.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BudgetRequest(
        @NotNull(message = "Category id is required")
        Long categoryId,

        @NotNull(message = "Month is required")
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        Integer month,

        @NotNull(message = "Year is required")
        @Min(value = 2000, message = "Year must be 2000 or later")
        @Max(value = 2100, message = "Year must be 2100 or earlier")
        Integer year,

        @NotNull(message = "Limit amount is required")
        @Positive(message = "Limit amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Limit must have at most 10 integer and 2 decimal digits")
        BigDecimal limitAmount) {
}
