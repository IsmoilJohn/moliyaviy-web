package com.moliyaviy.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryLimitStatusResponse(
        UUID categoryId,
        String categoryName,
        BigDecimal limit,
        BigDecimal spent,
        BigDecimal remaining,
        boolean exceeded
) {
}
