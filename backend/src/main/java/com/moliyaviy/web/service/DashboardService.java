package com.moliyaviy.web.service;

import com.moliyaviy.web.dto.CategoryExpenseResponse;
import com.moliyaviy.web.dto.CategoryLimitStatusResponse;
import com.moliyaviy.web.dto.DashboardResponse;
import com.moliyaviy.web.entity.Limit;
import com.moliyaviy.web.entity.TransactionType;
import com.moliyaviy.web.exception.ResourceNotFoundException;
import com.moliyaviy.web.repository.CategoryAmount;
import com.moliyaviy.web.repository.LimitRepository;
import com.moliyaviy.web.repository.TransactionRepository;
import com.moliyaviy.web.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int PERCENTAGE_SCALE = 2;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final LimitRepository limitRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId, YearMonth month) {
        ensureUserExists(userId);

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumAmount(userId, TransactionType.INCOME, start, end);
        BigDecimal totalExpense = transactionRepository.sumAmount(userId, TransactionType.EXPENSE, start, end);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        List<CategoryAmount> expenseByCategory =
                transactionRepository.sumAmountByCategory(userId, TransactionType.EXPENSE, start, end);

        Map<UUID, BigDecimal> spentByCategory = expenseByCategory.stream()
                .collect(Collectors.toMap(CategoryAmount::getCategoryId, CategoryAmount::getTotal));

        List<CategoryExpenseResponse> expensesByCategory = expenseByCategory.stream()
                .map(ca -> new CategoryExpenseResponse(
                        ca.getCategoryId(),
                        ca.getCategoryName(),
                        ca.getTotal(),
                        percentageOf(ca.getTotal(), totalExpense)))
                .sorted(Comparator.comparing(CategoryExpenseResponse::amount).reversed())
                .toList();

        List<CategoryLimitStatusResponse> limits = limitRepository.findAllByUserId(userId).stream()
                .map(limit -> toLimitStatus(limit, spentByCategory))
                .sorted(Comparator.comparing(CategoryLimitStatusResponse::categoryName))
                .toList();

        return new DashboardResponse(month.toString(), totalIncome, totalExpense, balance, expensesByCategory, limits);
    }

    private CategoryLimitStatusResponse toLimitStatus(Limit limit, Map<UUID, BigDecimal> spentByCategory) {
        BigDecimal spent = spentByCategory.getOrDefault(limit.getCategory().getId(), BigDecimal.ZERO);
        BigDecimal remaining = limit.getMonthlyLimit().subtract(spent);
        boolean exceeded = spent.compareTo(limit.getMonthlyLimit()) > 0;

        return new CategoryLimitStatusResponse(
                limit.getCategory().getId(),
                limit.getCategory().getName(),
                limit.getMonthlyLimit(),
                spent,
                remaining,
                exceeded);
    }

    private BigDecimal percentageOf(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100)).divide(total, PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
    }

}
