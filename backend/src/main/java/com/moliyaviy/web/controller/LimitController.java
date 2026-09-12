package com.moliyaviy.web.controller;

import com.moliyaviy.web.dto.LimitRequest;
import com.moliyaviy.web.dto.LimitResponse;
import com.moliyaviy.web.security.CurrentUser;
import com.moliyaviy.web.service.LimitService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/limits")
@RequiredArgsConstructor
public class LimitController {

    private final LimitService limitService;

    @GetMapping
    public List<LimitResponse> getAll(Authentication authentication) {
        return limitService.getAll(CurrentUser.id(authentication));
    }

    @GetMapping("/{id}")
    public LimitResponse getById(Authentication authentication, @PathVariable UUID id) {
        return limitService.getById(CurrentUser.id(authentication), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LimitResponse create(Authentication authentication, @Valid @RequestBody LimitRequest request) {
        return limitService.create(CurrentUser.id(authentication), request);
    }

    @PutMapping("/{id}")
    public LimitResponse update(Authentication authentication, @PathVariable UUID id,
                                 @Valid @RequestBody LimitRequest request) {
        return limitService.update(CurrentUser.id(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable UUID id) {
        limitService.delete(CurrentUser.id(authentication), id);
    }

}
