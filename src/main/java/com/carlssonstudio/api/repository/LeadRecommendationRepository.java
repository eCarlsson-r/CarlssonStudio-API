package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.LeadRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeadRecommendationRepository
        extends JpaRepository<LeadRecommendation, Long> {
    List<LeadRecommendation> findByLeadIdOrderByMatchScoreDesc(Long leadId);
}