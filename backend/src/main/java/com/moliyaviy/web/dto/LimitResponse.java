package com.moliyaviy.web.dto;

import com.moliyaviy.web.entity.Limit;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LimitResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        BigDecimal monthlyLimit,
        Instant createdAt,
        Instant updatedAt
) {

    public static LimitResponse from(Limit limit) {
        return new LimitResponse(
                limit.getId(),
                limit.getCategory().getId(),
                limit.getCategory().getName(),
                limit.getMonthlyLimit(),
                limit.getCreatedAt(),
                limit.getUpdatedAt()
        );
    }

}
