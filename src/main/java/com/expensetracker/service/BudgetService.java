package com.expensetracker.service;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.dto.BudgetResponse;
import com.expensetracker.dto.BudgetStatusResponse;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.DuplicateResourceException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal NEAR_LIMIT_PERCENT = BigDecimal.valueOf(80);

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository,
                         CategoryService categoryService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public BudgetResponse create(BudgetRequest request) {
        Category category = categoryService.getEntity(request.categoryId());
        if (budgetRepository.existsByCategoryIdAndMonthAndYear(category.getId(), request.month(), request.year())) {
            throw new DuplicateResourceException("A budget already exists for this category and month");
        }
        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setLimitAmount(request.limitAmount());
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> findAll(Integer month, Integer year) {
        List<Budget> budgets;
        if (month != null && year != null) {
            budgets = budgetRepository.findByMonthAndYear(month, year);
        } else if (month == null && year != null) {
            budgets = budgetRepository.findByYear(year);
        } else if (month != null) {
            throw new BadRequestException("Month filter requires a year");
        } else {
            budgets = budgetRepository.findAll();
        }
        return budgets.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public BudgetResponse update(Long id, BudgetRequest request) {
        Budget budget = getEntity(id);
        Category category = categoryService.getEntity(request.categoryId());
        if (budgetRepository.existsByCategoryIdAndMonthAndYearAndIdNot(
                category.getId(), request.month(), request.year(), id)) {
            throw new DuplicateResourceException("A budget already exists for this category and month");
        }
        budget.setCategory(category);
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setLimitAmount(request.limitAmount());
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(Long id) {
        budgetRepository.delete(getEntity(id));
    }

    /** Compares actual EXPENSE spending against each budget for the given month. */
    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> status(int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        return budgetRepository.findByMonthAndYear(month, year).stream().map(b -> {
            BigDecimal spent = transactionRepository.sumByTypeAndCategoryAndPeriod(
                    TransactionType.EXPENSE, b.getCategory().getId(), ym.atDay(1), ym.atEndOfMonth());
            if (spent == null) {
                spent = BigDecimal.ZERO;
            }
            BigDecimal limit = b.getLimitAmount();
            BigDecimal percent = spent.multiply(HUNDRED).divide(limit, 2, RoundingMode.HALF_UP);
            String status = spent.compareTo(limit) > 0 ? "EXCEEDED"
                    : percent.compareTo(NEAR_LIMIT_PERCENT) >= 0 ? "NEAR_LIMIT" : "WITHIN_BUDGET";
            return new BudgetStatusResponse(b.getId(), b.getCategory().getName(), month, year,
                    limit, spent, limit.subtract(spent), percent, status);
        }).toList();
    }

    private Budget getEntity(Long id) {
        return budgetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Budget", id));
    }

    private BudgetResponse toResponse(Budget b) {
        return new BudgetResponse(b.getId(), b.getCategory().getId(), b.getCategory().getName(),
                b.getMonth(), b.getYear(), b.getLimitAmount());
    }
}
