package com.moliyaviy.web.repository;

import com.moliyaviy.web.entity.Limit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LimitRepository extends JpaRepository<Limit, UUID> {

    List<Limit> findAllByUserId(UUID userId);

    Optional<Limit> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByCategoryId(UUID categoryId);

}
