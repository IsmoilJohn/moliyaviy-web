package com.moliyaviy.web.repository;

import com.moliyaviy.web.entity.Transaction;
import com.moliyaviy.web.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByUserIdOrderByTransactionDateDesc(UUID userId);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByCategoryId(UUID categoryId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId AND t.type = :type
              AND t.transactionDate BETWEEN :start AND :end
            """)
    BigDecimal sumAmount(@Param("userId") UUID userId, @Param("type") TransactionType type,
                          @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            SELECT t.category.id AS categoryId, t.category.name AS categoryName, SUM(t.amount) AS total
            FROM Transaction t
            WHERE t.user.id = :userId AND t.type = :type
              AND t.transactionDate BETWEEN :start AND :end
            GROUP BY t.category.id, t.category.name
            """)
    List<CategoryAmount> sumAmountByCategory(@Param("userId") UUID userId, @Param("type") TransactionType type,
                                              @Param("start") LocalDate start, @Param("end") LocalDate end);

}
