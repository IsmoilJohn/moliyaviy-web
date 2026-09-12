package com.moliyaviy.web.service;

import com.moliyaviy.web.dto.TransactionRequest;
import com.moliyaviy.web.dto.TransactionResponse;
import com.moliyaviy.web.entity.Category;
import com.moliyaviy.web.entity.Transaction;
import com.moliyaviy.web.entity.User;
import com.moliyaviy.web.exception.ResourceNotFoundException;
import com.moliyaviy.web.repository.CategoryRepository;
import com.moliyaviy.web.repository.TransactionRepository;
import com.moliyaviy.web.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(UUID userId) {
        ensureUserExists(userId);
        return transactionRepository.findAllByUserIdOrderByTransactionDateDesc(userId).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(UUID userId, UUID id) {
        return TransactionResponse.from(findOwnedTransaction(userId, id));
    }

    @Transactional
    public TransactionResponse create(UUID userId, TransactionRequest request) {
        User user = findUser(userId);
        Category category = findOwnedCategory(userId, request.categoryId());

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        applyRequest(transaction, category, request);

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse update(UUID userId, UUID id, TransactionRequest request) {
        Transaction transaction = findOwnedTransaction(userId, id);
        Category category = findOwnedCategory(userId, request.categoryId());

        applyRequest(transaction, category, request);

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        transactionRepository.delete(findOwnedTransaction(userId, id));
    }

    private void applyRequest(Transaction transaction, Category category, TransactionRequest request) {
        transaction.setCategory(category);
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setComment(request.comment());
    }

    private Transaction findOwnedTransaction(UUID userId, UUID id) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
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
