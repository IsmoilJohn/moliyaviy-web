package com.moliyaviy.web.repository;

import com.moliyaviy.web.entity.Category;
import com.moliyaviy.web.entity.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByUserIdOrderByNameAsc(UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndNameIgnoreCaseAndType(UUID userId, String name, TransactionType type);

}
