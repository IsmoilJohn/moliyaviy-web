package com.moliyaviy.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryExpenseResponse(
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        BigDecimal percentage
) {
}
