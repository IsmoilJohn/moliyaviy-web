package com.moliyaviy.web.dto;

import com.moliyaviy.web.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull TransactionType type,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "must be a hex color like #RRGGBB") String color
) {
}
