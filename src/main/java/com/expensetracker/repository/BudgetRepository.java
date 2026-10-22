package com.expensetracker.repository;

import com.expensetracker.entity.Budget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    boolean existsByCategoryIdAndMonthAndYear(Long categoryId, Integer month, Integer year);
    boolean existsByCategoryIdAndMonthAndYearAndIdNot(Long categoryId, Integer month, Integer year, Long id);
    boolean existsByCategoryId(Long categoryId);
    List<Budget> findByMonthAndYear(Integer month, Integer year);
    List<Budget> findByYear(Integer year);
}
