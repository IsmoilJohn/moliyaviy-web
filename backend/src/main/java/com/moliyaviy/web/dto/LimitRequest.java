package com.moliyaviy.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record LimitRequest(
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal monthlyLimit
) {
}
