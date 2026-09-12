package com.moliyaviy.web.dto;

import com.moliyaviy.web.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(
        @NotNull UUID categoryId,
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate transactionDate,
        @Size(max = 500) String comment
) {
}
