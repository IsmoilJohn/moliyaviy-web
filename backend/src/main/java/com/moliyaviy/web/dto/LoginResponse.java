package com.moliyaviy.web.dto;

public record LoginResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
