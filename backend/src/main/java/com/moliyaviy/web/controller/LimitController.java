package com.moliyaviy.web.controller;

import com.moliyaviy.web.dto.LimitRequest;
import com.moliyaviy.web.dto.LimitResponse;
import com.moliyaviy.web.service.LimitService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/limits")
@RequiredArgsConstructor
public class LimitController {

    private final LimitService limitService;

    @GetMapping
    public List<LimitResponse> getAll(@PathVariable UUID userId) {
        return limitService.getAll(userId);
    }

    @GetMapping("/{id}")
    public LimitResponse getById(@PathVariable UUID userId, @PathVariable UUID id) {
        return limitService.getById(userId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LimitResponse create(@PathVariable UUID userId, @Valid @RequestBody LimitRequest request) {
        return limitService.create(userId, request);
    }

    @PutMapping("/{id}")
    public LimitResponse update(@PathVariable UUID userId, @PathVariable UUID id,
                                 @Valid @RequestBody LimitRequest request) {
        return limitService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId, @PathVariable UUID id) {
        limitService.delete(userId, id);
    }

}
