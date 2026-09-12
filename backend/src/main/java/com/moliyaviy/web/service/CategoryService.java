package com.moliyaviy.web.service;

import com.moliyaviy.web.dto.CategoryRequest;
import com.moliyaviy.web.dto.CategoryResponse;
import com.moliyaviy.web.entity.Category;
import com.moliyaviy.web.entity.TransactionType;
import com.moliyaviy.web.entity.User;
import com.moliyaviy.web.exception.CategoryInUseException;
import com.moliyaviy.web.exception.DuplicateResourceException;
import com.moliyaviy.web.exception.ResourceNotFoundException;
import com.moliyaviy.web.repository.CategoryRepository;
import com.moliyaviy.web.repository.LimitRepository;
import com.moliyaviy.web.repository.TransactionRepository;
import com.moliyaviy.web.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final LimitRepository limitRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(UUID userId) {
        ensureUserExists(userId);
        return categoryRepository.findAllByUserIdOrderByNameAsc(userId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID userId, UUID id) {
        return CategoryResponse.from(findOwnedCategory(userId, id));
    }

    @Transactional
    public CategoryResponse create(UUID userId, CategoryRequest request) {
        User user = findUser(userId);
        ensureNameAvailable(userId, request.name(), request.type(), null);

        Category category = new Category();
        category.setUser(user);
        applyRequest(category, request);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(UUID userId, UUID id, CategoryRequest request) {
        Category category = findOwnedCategory(userId, id);
        ensureNameAvailable(userId, request.name(), request.type(), category.getId());

        applyRequest(category, request);

        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        Category category = findOwnedCategory(userId, id);

        if (transactionRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException(
                    "Category has existing transactions and cannot be deleted: " + id);
        }
        if (limitRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException(
                    "Category has a monthly limit and cannot be deleted: " + id);
        }

        categoryRepository.delete(category);
    }

    private void applyRequest(Category category, CategoryRequest request) {
        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());
    }

    private void ensureNameAvailable(UUID userId, String name, TransactionType type, UUID excludeCategoryId) {
        boolean nameTaken = categoryRepository.existsByUserIdAndNameIgnoreCaseAndType(userId, name, type);
        if (!nameTaken) {
            return;
        }
        if (excludeCategoryId != null) {
            Category existing = findOwnedCategory(userId, excludeCategoryId);
            boolean sameCategory = existing.getName().equalsIgnoreCase(name) && existing.getType() == type;
            if (sameCategory) {
                return;
            }
        }
        throw new DuplicateResourceException(
                "Category with name '" + name + "' and type " + type + " already exists");
    }

    private Category findOwnedCategory(UUID userId, UUID id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
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
