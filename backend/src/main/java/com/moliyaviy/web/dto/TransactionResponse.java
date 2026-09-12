package com.moliyaviy.web.dto;

import com.moliyaviy.web.entity.Transaction;
import com.moliyaviy.web.entity.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        TransactionType type,
        BigDecimal amount,
        LocalDate transactionDate,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getComment(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

}
