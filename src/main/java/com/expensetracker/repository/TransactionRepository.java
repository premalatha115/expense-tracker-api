package com.expensetracker.repository;

import com.expensetracker.dto.CategoryTotal;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    boolean existsByCategoryId(Long categoryId);

    @Query("select sum(t.amount) from Transaction t "
            + "where t.type = :type and t.transactionDate between :start and :end")
    BigDecimal sumByTypeAndPeriod(@Param("type") TransactionType type,
                                  @Param("start") LocalDate start,
                                  @Param("end") LocalDate end);

    @Query("select sum(t.amount) from Transaction t "
            + "where t.type = :type and t.category.id = :categoryId "
            + "and t.transactionDate between :start and :end")
    BigDecimal sumByTypeAndCategoryAndPeriod(@Param("type") TransactionType type,
                                             @Param("categoryId") Long categoryId,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);

    @Query("select new com.expensetracker.dto.CategoryTotal(t.category.name, sum(t.amount)) "
            + "from Transaction t "
            + "where t.type = :type and t.transactionDate between :start and :end "
            + "group by t.category.name order by sum(t.amount) desc")
    List<CategoryTotal> totalsByCategory(@Param("type") TransactionType type,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);
}
