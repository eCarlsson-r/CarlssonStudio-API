package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.LeadRequest;
import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.entity.BusinessStatus;
import com.carlssonstudio.api.entity.Lead;
import com.carlssonstudio.api.entity.LeadStatus;
import com.carlssonstudio.api.recommendation.RecommendationEngine;
import com.carlssonstudio.api.repository.FoundationRepository;
import com.carlssonstudio.api.repository.LeadRecommendationRepository;
import com.carlssonstudio.api.repository.LeadRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeadServiceTest {

    @Test
    void submitIncludesOperationalAssessmentInResponse() {
        LeadRepository leadRepository = mock(LeadRepository.class);
        FoundationRepository foundationRepository = mock(FoundationRepository.class);
        LeadRecommendationRepository recommendationRepository = mock(LeadRecommendationRepository.class);
        RecommendationEngine recommendationEngine = mock(RecommendationEngine.class);
        NotificationService notificationService = mock(NotificationService.class);
        MetaConversionsApiService metaConversionsApiService = mock(MetaConversionsApiService.class);

        Lead lead = Lead.builder()
                .id(42L)
                .name("Test Lead")
                .company("Carlsson Studio")
                .companySize("5-20")
                .industry("Retail")
                .buildType("ERP")
                .problems(List.of("Manual spreadsheets"))
                .features(List.of("Dashboard"))
                .businessStatus(BusinessStatus.RUNNING)
                .status(LeadStatus.NEW)
                .build();

        when(leadRepository.save(any(Lead.class))).thenReturn(lead);
        when(recommendationEngine.recommend(any(LeadRequest.class), any(Locale.class))).thenReturn(List.of());

        LeadService service = new LeadService(
                leadRepository,
                foundationRepository,
                recommendationRepository,
                recommendationEngine,
                notificationService,
                metaConversionsApiService
        );

        LeadRequest request = new LeadRequest();
        request.setName("Test Lead");
        request.setEmail("test@example.com");
        request.setCompany("Carlsson Studio");
        request.setCompanySize("5-20");
        request.setIndustry("Retail");
        request.setBuildType("ERP");
        request.setProblems(List.of("Manual spreadsheets"));
        request.setFeatures(List.of("Dashboard"));
        request.setBusinessStatus(BusinessStatus.RUNNING);

        LeadResponse response = service.submit(request);

        assertNotNull(response.getOperationalAssessment());
        assertTrue(response.getOperationalAssessment().getScore() >= 35);
        assertNotNull(response.getOperationalAssessment().getSummary());
    }
}
