package com.moliyaviy.web.controller;

import com.moliyaviy.web.dto.CategoryRequest;
import com.moliyaviy.web.dto.CategoryResponse;
import com.moliyaviy.web.service.CategoryService;
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
@RequestMapping("/api/users/{userId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAll(@PathVariable UUID userId) {
        return categoryService.getAll(userId);
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable UUID userId, @PathVariable UUID id) {
        return categoryService.getById(userId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@PathVariable UUID userId, @Valid @RequestBody CategoryRequest request) {
        return categoryService.create(userId, request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID userId, @PathVariable UUID id,
                                    @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId, @PathVariable UUID id) {
        categoryService.delete(userId, id);
    }

}
