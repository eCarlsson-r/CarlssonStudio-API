package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.LeadRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeadRecommendationRepository extends JpaRepository<LeadRecommendation, Long> {
    List<LeadRecommendation> findByLeadIdOrderByMatchScoreDesc(Long leadId);
    
    @Query("SELECT r.foundationSlug, COUNT(r), AVG(r.matchScore) " +
	       "FROM LeadRecommendation r GROUP BY r.foundationSlug " +
	       "ORDER BY COUNT(r) DESC")
	List<Object[]> foundationPopularity();

	@Query("SELECT r.foundationSlug, COUNT(r) FROM LeadRecommendation r " +
	       "WHERE r.matchScore = (SELECT MAX(r2.matchScore) " +
	       "FROM LeadRecommendation r2 WHERE r2.lead.id = r.lead.id) " +
	       "GROUP BY r.foundationSlug")
	List<Object[]> topMatchCounts();
}