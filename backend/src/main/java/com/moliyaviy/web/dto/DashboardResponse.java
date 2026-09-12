package com.moliyaviy.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategoryExpenseResponse> expensesByCategory,
        List<CategoryLimitStatusResponse> limits
) {
}
