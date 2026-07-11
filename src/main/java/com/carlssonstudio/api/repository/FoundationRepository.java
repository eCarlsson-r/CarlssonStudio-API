package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.FoundationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FoundationRepository extends JpaRepository<FoundationEntity, Long> {
    List<FoundationEntity> findByActiveTrue();
    Optional<FoundationEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}