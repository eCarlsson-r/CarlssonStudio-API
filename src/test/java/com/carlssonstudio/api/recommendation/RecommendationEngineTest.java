package com.carlssonstudio.api.recommendation;

import com.carlssonstudio.api.dto.LeadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationEngineTest {

    private RecommendationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new RecommendationEngine(
                new FoundationRegistry());
    }

    @Test
    void restaurantPosLeadShouldMatchRestoSystemFirst() {
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Restaurant");
        request.setBuildType("POS");
        request.setProblems(List.of(
                "Manual spreadsheets", "No reporting",
                "No booking"));
        request.setFeatures(List.of(
                "Dashboard", "Reports", "Authentication",
                "Scheduling"));

        List<ScoringResult> results = engine.recommend(request);

        assertEquals(3, results.size());
        assertEquals("resto-system",
                results.get(0).getFoundation().getSlug());
        assertEquals(100, results.get(0).getScore());
    }

    @Test
    void resultsAreSortedByScoreDescending() {
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Wellness");
        request.setBuildType("Booking");
        request.setProblems(List.of("No booking"));
        request.setFeatures(List.of("Scheduling"));

        List<ScoringResult> results = engine.recommend(request);

        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getScore() >= results.get(i + 1).getScore());
        }
        assertEquals("spa-system",
                results.get(0).getFoundation().getSlug());
    }

    @Test
    void unknownIndustryStillReturnsThreeResults() {
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Mining");
        request.setBuildType("ERP");
        request.setProblems(List.of("Manual spreadsheets"));
        request.setFeatures(List.of("Dashboard"));

        List<ScoringResult> results = engine.recommend(request);

        assertEquals(3, results.size());
        // No perfect match — top score should be below 100
        assertTrue(results.get(0).getScore() < 100);
    }

    @Test
    void scoreNeverExceedsHundred() {
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Restaurant");
        request.setBuildType("POS");
        request.setProblems(List.of(
                "Manual spreadsheets", "No reporting",
                "No inventory", "No booking",
                "Duplicate work", "No dashboard"));
        request.setFeatures(List.of(
                "Authentication", "Roles", "Dashboard",
                "Reports", "Notifications", "AI",
                "Scheduling", "API"));

        List<ScoringResult> results = engine.recommend(request);

        results.forEach(r -> assertTrue(r.getScore() <= 100));
    }
}