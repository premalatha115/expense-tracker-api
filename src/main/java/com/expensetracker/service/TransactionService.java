package com.expensetracker.service;

import com.expensetracker.dto.TransactionRequest;
import com.expensetracker.dto.TransactionResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.TransactionRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        Transaction t = new Transaction();
        apply(t, request);
        return toResponse(transactionRepository.save(t));
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> search(TransactionType type, Long categoryId, Integer month, Integer year) {
        if ((month == null) != (year == null)) {
            throw new BadRequestException("Provide both month and year, or neither");
        }
        LocalDate start = null;
        LocalDate end = null;
        if (month != null) {
            YearMonth ym = YearMonth.of(year, month);
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        }
        final LocalDate from = start;
        final LocalDate to = end;

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<LocalDate>get("transactionDate"), from));
                predicates.add(cb.lessThanOrEqualTo(root.<LocalDate>get("transactionDate"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "transactionDate", "id");
        return transactionRepository.findAll(spec, sort).stream().map(this::toResponse).toList();
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction t = getEntity(id);
        apply(t, request);
        return toResponse(transactionRepository.save(t));
    }

    @Transactional
    public void delete(Long id) {
        transactionRepository.delete(getEntity(id));
    }

    private void apply(Transaction t, TransactionRequest r) {
        Category category = categoryService.getEntity(r.categoryId());
        t.setType(r.type());
        t.setAmount(r.amount());
        t.setDescription(r.description());
        t.setTransactionDate(r.date());
        t.setCategory(category);
    }

    private Transaction getEntity(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(t.getId(), t.getType(), t.getAmount(), t.getDescription(),
                t.getTransactionDate(), t.getCategory().getId(), t.getCategory().getName());
    }
}
