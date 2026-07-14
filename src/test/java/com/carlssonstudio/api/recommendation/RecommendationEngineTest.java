package com.carlssonstudio.api.recommendation;

import com.carlssonstudio.api.dto.LeadRequest;
import com.carlssonstudio.api.entity.FoundationEntity;
import com.carlssonstudio.api.repository.FoundationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationEngineTest {

    private RecommendationEngine engine;
    
    private List<FoundationEntity> testFoundations() {
        return List.of(
            FoundationEntity.builder()
                .slug("resto-system")
                .name("RestoSystem")
                .industry("Restaurant")
                .relatedIndustries(List.of("Restaurant", "Cafe",
                    "Food & Beverage", "Catering"))
                .buildTypes(List.of("POS", "ERP", "Booking",
                    "Internal System", "Dashboard"))
                .problems(List.of("Manual spreadsheets", "No reporting",
                    "No inventory", "No booking", "Duplicate work",
                    "No dashboard"))
                .features(List.of("Authentication", "Roles", "Inventory",
                    "Dashboard", "Reports", "Notifications", "AI",
                    "Scheduling", "API"))
                .active(true)
                .build(),
            FoundationEntity.builder()
                .slug("commerce-system")
                .name("CommerceSystem")
                .industry("Retail")
                .relatedIndustries(List.of("Retail", "E-Commerce", "Wholesale", "Fashion"))
                .buildTypes(List.of("POS", "ERP", "E-Commerce",
                		"Internal System", "Customer Portal"))
                .problems(List.of("Manual spreadsheets", "No inventory",
                		"No reporting", "Duplicate work", "No dashboard"))
                .features(List.of("Authentication", "Roles", "Payments",
                		"Inventory", "Reports", "Dashboard", "Invoices",
                		"API", "Notifications"))
                .active(true)
                .build(),
            FoundationEntity.builder()
                .slug("urus-properti")
                .name("UrusProperti")
                .industry("Property")
                .relatedIndustries(List.of("Property", "Real Estate", "Construction", "Leasing"))
                .buildTypes(List.of("Internal System", "Customer Portal", "Dashboard"))
                .problems(List.of("Manual spreadsheets", "No reporting", 
                		"Duplicate work", "No dashboard", "WhatsApp chaos"))
                .features(List.of("Authentication", "Roles", "Dashboard",
                		"Reports", "Notifications", "AI", "API", "Invoices"))
                .active(true)
                .build(),
            FoundationEntity.builder()
                .slug("insurance-portal")
                .name("InsurancePortal")
                .industry("Insurance")
                .relatedIndustries(List.of("Insurance", "Finance", "Banking", "Financial Services"))
                .buildTypes(List.of("Internal System", "Customer Portal", "Dashboard"))
                .problems(List.of("Manual spreadsheets", "No reporting", "Duplicate work",
                		"No dashboard", "WhatsApp chaos"))
                .features(List.of("Authentication", "Roles", "Dashboard", "Reports", 
                		"AI", "API", "Notifications", "Mobile"))
                .active(true)
                .build(),
            FoundationEntity.builder()
                .slug("spa-system")
                .name("SpaSystem")
                .industry("Wellness")
                .relatedIndustries(List.of("Wellness","Spa","Beauty","Healthcare","Fitness"))
                .buildTypes(List.of("Booking","ERP","Internal System","Customer Portal"))
                .problems(List.of("No booking", "Manual spreadsheets",
                		"No inventory","WhatsApp chaos", "No reporting"))
                .features(List.of("Authentication", "Roles", "Scheduling", "Payments",
                		"Inventory", "Notifications", "Reports", "Dashboard", "Mobile"))
	            .active(true)
	            .build(),
	        FoundationEntity.builder()
	            .slug("payroll-agent")
	            .name("Payroll Agent")
                .industry("HR & Payroll")
	            .relatedIndustries(List.of("HR & Payroll","Manufacturing","Professional Services","Education","Healthcare"))
	            .buildTypes(List.of("Internal System","ERP","Dashboard"))
	            .problems(List.of("Manual spreadsheets", "No HR", 
        		 "Duplicate work", "No reporting", "No dashboard"))
                .features(List.of("Authentication","Roles","Reports","Dashboard","API","Notifications","AI","Invoices"))
                .active(true)
                .build(),
            FoundationEntity.builder()
                .slug("human-design")
                .name("HumanDesign")
                .industry("Recruitment")
                .relatedIndustries(List.of("Recruitment","HR & Payroll","Professional Services","Consulting","Agencies","Education","AI"))
                .buildTypes(List.of("AI Assistant","Customer Portal","Internal System","Dashboard"))
                .problems(List.of("Hiring mismatches","No candidate assessment","Manual spreadsheets","No reporting","No dashboard","Duplicate work"))
                .features(List.of("Authentication","AI","API","Reports","Dashboard","Mobile"))
                .active(true)
                .build(),
            FoundationEntity.builder()
                .slug("quoteplot-agent")
                .name("QuotePlot Agent")
                .industry("AI")
                .relatedIndustries(List.of("AI","Finance","Investment","Banking","Professional Services"))
                .buildTypes(List.of("AI Assistant","Dashboard","Internal System"))
                .problems(List.of("No reporting", "No dashboard",
                		 "Manual spreadsheets", "Duplicate work"))
                .features(List.of("Authentication","AI","API","Dashboard","Reports","Notifications"))
                .active(true)
                .build()
        );
    }

    @BeforeEach
    void setUp() {
    	FoundationRepository fakeRepo =
    		    org.mockito.Mockito.mock(FoundationRepository.class);
    		org.mockito.Mockito.when(fakeRepo.findByActiveTrue())
    		    .thenReturn(testFoundations());
    		engine = new RecommendationEngine(fakeRepo);
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
    void retailEcommerceLeadShouldMatchCommerceSystemFirst() {
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Retail");
        request.setBuildType("E-Commerce");
        request.setProblems(List.of(
                "No inventory", "Manual spreadsheets"));
        request.setFeatures(List.of(
                "Inventory", "Payments", "Dashboard"));

        List<ScoringResult> results = engine.recommend(request);

        assertEquals("commerce-system",
                results.get(0).getFoundation().getSlug());
        assertTrue(results.get(0).getScore() >= 90);
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