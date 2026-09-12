package com.moliyaviy.web.controller;

import com.moliyaviy.web.dto.TransactionRequest;
import com.moliyaviy.web.dto.TransactionResponse;
import com.moliyaviy.web.service.TransactionService;
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
@RequestMapping("/api/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponse> getAll(@PathVariable UUID userId) {
        return transactionService.getAll(userId);
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable UUID userId, @PathVariable UUID id) {
        return transactionService.getById(userId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@PathVariable UUID userId, @Valid @RequestBody TransactionRequest request) {
        return transactionService.create(userId, request);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable UUID userId, @PathVariable UUID id,
                                       @Valid @RequestBody TransactionRequest request) {
        return transactionService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId, @PathVariable UUID id) {
        transactionService.delete(userId, id);
    }

}
