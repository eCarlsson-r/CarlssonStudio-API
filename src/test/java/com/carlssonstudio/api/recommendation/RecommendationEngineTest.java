package com.carlssonstudio.api.recommendation;

import com.carlssonstudio.api.dto.LeadRequest;
import com.carlssonstudio.api.entity.FoundationEntity;
import com.carlssonstudio.api.repository.FoundationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationEngineTest {

    private RecommendationEngine engine;

    /**
     * Real bundle, not a stub — exercises the actual messages.properties /
     * messages_id.properties on the classpath so a typo'd or missing key
     * fails here instead of only surfacing at runtime.
     */
    private static ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(false);
        return source;
    }

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
                // Real subset from CommerceSystem-API/modules-export.json
                .moduleIndustries(List.of("pharmacy", "hardware-stores", "wholesale"))
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
                // Real subset from InsurancePortal/modules-export.json
                .moduleIndustries(List.of("legal", "travel", "wealth-management"))
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
                // Real subset unioned from the 3 Spa repos' modules-export.json
                .moduleIndustries(List.of("salon_and_beauty", "healthcare_clinics", "warehousing"))
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
                .moduleIndustries(List.of("professional services", "healthcare staffing", "education"))
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
    		engine = new RecommendationEngine(fakeRepo, messageSource());
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

        List<ScoringResult> results = engine.recommend(request, Locale.ENGLISH);

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

        List<ScoringResult> results = engine.recommend(request, Locale.ENGLISH);

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

        List<ScoringResult> results = engine.recommend(request, Locale.ENGLISH);

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

        List<ScoringResult> results = engine.recommend(request, Locale.ENGLISH);

        assertEquals("commerce-system",
                results.get(0).getFoundation().getSlug());
        assertTrue(results.get(0).getScore() >= 90);
    }

    @Test
    void indonesianLocaleReturnsLocalizedReasonWithSameScore() {
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Restaurant");
        request.setBuildType("POS");
        request.setProblems(List.of("Manual spreadsheets", "No reporting"));
        request.setFeatures(List.of("Reports"));

        Locale indonesian = Locale.forLanguageTag("id");
        List<ScoringResult> resultsId = engine.recommend(request, indonesian);
        List<ScoringResult> resultsEn = engine.recommend(request, Locale.ENGLISH);

        ScoringResult topId = resultsId.get(0);
        ScoringResult topEn = resultsEn.get(0);

        // Locale changes the reason text only — scoring/matching is unaffected.
        assertEquals("resto-system", topId.getFoundation().getSlug());
        assertEquals(topEn.getScore(), topId.getScore());

        String reason = topId.getReason();
        assertTrue(reason.contains("Restoran"),
            "expected the Indonesian industry label \"Restoran\" in: " + reason);
        assertTrue(reason.contains("Sistem Kasir (POS)"),
            "expected the Indonesian build-type label in: " + reason);
        assertTrue(reason.contains("Masih pakai Excel"),
            "expected the Indonesian problem label in: " + reason);
        assertTrue(reason.contains("Laporan"),
            "expected the Indonesian feature label in: " + reason);
        assertFalse(reason.contains("is purpose-built"),
            "reason should not fall back to English: " + reason);
    }

    @Test
    void moduleIndustryTagAwardsPartialCreditWhenNoCuratedMatch() {
        // "Pharmacy" isn't CommerceSystem's industry ("Retail") nor one of
        // its curated relatedIndustries — only a module-level tag. Build
        // type, problems, and features are deliberately unmatchable by any
        // foundation so the industry tier is the only differentiator.
        LeadRequest request = new LeadRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setIndustry("Pharmacy");
        request.setBuildType("Something Else");
        request.setProblems(List.of("Nonexistent problem"));
        request.setFeatures(List.of("Nonexistent feature"));

        List<ScoringResult> results = engine.recommend(request, Locale.ENGLISH);
        ScoringResult top = results.get(0);

        assertEquals("commerce-system", top.getFoundation().getSlug());
        // industry 25 (module match) * 40% = 10; buildType 20 (no match,
        // non-blank) * 25% = 5; problems/features 0. Total = 15.
        assertEquals(15, top.getScore());
        assertTrue(top.getReason().contains("proven, portable module"),
            "expected the module-match reason fragment in: " + top.getReason());
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

        List<ScoringResult> results = engine.recommend(request, Locale.ENGLISH);

        results.forEach(r -> assertTrue(r.getScore() <= 100));
    }
}