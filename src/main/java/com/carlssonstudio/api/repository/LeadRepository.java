package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.Lead;
import com.carlssonstudio.api.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatus(LeadStatus status);
    List<Lead> findByIndustry(String industry);
    boolean existsByEmail(String email);
    
    long countByCreatedAtAfter(LocalDateTime dateTime);

    @Query("SELECT l.status AS k, COUNT(l) AS v FROM Lead l GROUP BY l.status")
    List<Object[]> countGroupByStatus();

    @Query("SELECT l.industry AS k, COUNT(l) AS v FROM Lead l GROUP BY l.industry")
    List<Object[]> countGroupByIndustry();
    
    @Query("SELECT l.source AS k, COUNT(l) AS v FROM Lead l GROUP BY l.source")
    List<Object[]> countGroupBySource();

    @Query("SELECT FUNCTION('DATE', l.createdAt) AS d, COUNT(l) AS c " +
           "FROM Lead l WHERE l.createdAt >= :since " +
           "GROUP BY FUNCTION('DATE', l.createdAt) ORDER BY d")
    List<Object[]> countPerDaySince(
        @Param("since") LocalDateTime since);
}