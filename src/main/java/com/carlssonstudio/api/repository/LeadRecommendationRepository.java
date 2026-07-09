package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.LeadRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeadRecommendationRepository
        extends JpaRepository<LeadRecommendation, Long> {
    List<LeadRecommendation> findByLeadIdOrderByMatchScoreDesc(Long leadId);
}