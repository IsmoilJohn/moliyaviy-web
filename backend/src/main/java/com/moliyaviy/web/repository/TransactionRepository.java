package com.moliyaviy.web.repository;

import com.moliyaviy.web.entity.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByUserIdOrderByTransactionDateDesc(UUID userId);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

}
