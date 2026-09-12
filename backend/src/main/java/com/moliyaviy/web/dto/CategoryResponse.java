package com.moliyaviy.web.dto;

import com.moliyaviy.web.entity.Category;
import com.moliyaviy.web.entity.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        TransactionType type,
        String color,
        Instant createdAt,
        Instant updatedAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getColor(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

}
