package com.expensetracker.service;

import com.expensetracker.dto.MonthlySummaryResponse;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SummaryService {

    private final TransactionRepository transactionRepository;

    public SummaryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse monthly(int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal income = orZero(transactionRepository.sumByTypeAndPeriod(TransactionType.INCOME, start, end));
        BigDecimal expense = orZero(transactionRepository.sumByTypeAndPeriod(TransactionType.EXPENSE, start, end));

        return new MonthlySummaryResponse(month, year, income, expense, income.subtract(expense),
                transactionRepository.totalsByCategory(TransactionType.EXPENSE, start, end),
                transactionRepository.totalsByCategory(TransactionType.INCOME, start, end));
    }

    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
