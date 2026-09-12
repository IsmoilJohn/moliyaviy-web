package com.moliyaviy.web.service;

import com.moliyaviy.web.dto.LimitRequest;
import com.moliyaviy.web.dto.LimitResponse;
import com.moliyaviy.web.entity.Category;
import com.moliyaviy.web.entity.Limit;
import com.moliyaviy.web.entity.User;
import com.moliyaviy.web.exception.DuplicateResourceException;
import com.moliyaviy.web.exception.ResourceNotFoundException;
import com.moliyaviy.web.repository.CategoryRepository;
import com.moliyaviy.web.repository.LimitRepository;
import com.moliyaviy.web.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LimitService {

    private final LimitRepository limitRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<LimitResponse> getAll(UUID userId) {
        ensureUserExists(userId);
        return limitRepository.findAllByUserId(userId).stream()
                .map(LimitResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LimitResponse getById(UUID userId, UUID id) {
        return LimitResponse.from(findOwnedLimit(userId, id));
    }

    @Transactional
    public LimitResponse create(UUID userId, LimitRequest request) {
        User user = findUser(userId);
        Category category = findOwnedCategory(userId, request.categoryId());

        if (limitRepository.existsByCategoryId(category.getId())) {
            throw new DuplicateResourceException("Limit already exists for category: " + category.getId());
        }

        Limit limit = new Limit();
        limit.setUser(user);
        limit.setCategory(category);
        limit.setMonthlyLimit(request.monthlyLimit());

        return LimitResponse.from(limitRepository.save(limit));
    }

    @Transactional
    public LimitResponse update(UUID userId, UUID id, LimitRequest request) {
        Limit limit = findOwnedLimit(userId, id);
        Category category = findOwnedCategory(userId, request.categoryId());

        boolean categoryChanged = !limit.getCategory().getId().equals(category.getId());
        if (categoryChanged && limitRepository.existsByCategoryId(category.getId())) {
            throw new DuplicateResourceException("Limit already exists for category: " + category.getId());
        }

        limit.setCategory(category);
        limit.setMonthlyLimit(request.monthlyLimit());

        return LimitResponse.from(limit);
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        limitRepository.delete(findOwnedLimit(userId, id));
    }

    private Limit findOwnedLimit(UUID userId, UUID id) {
        return limitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found: " + id));
    }

    private Category findOwnedCategory(UUID userId, UUID categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
    }

}
