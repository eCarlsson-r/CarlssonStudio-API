package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.entity.*;
import com.carlssonstudio.api.recommendation.*;
import com.carlssonstudio.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final FoundationRepository foundationRepository;
    private final LeadRecommendationRepository recommendationRepository;
    private final RecommendationEngine recommendationEngine;
    private final NotificationService notificationService;
    private final MetaConversionsApiService metaConversionsApiService;

    @Transactional
    public LeadResponse submit(LeadRequest request) {
        return submit(request, null, null, Locale.ENGLISH);
    }

    @Transactional
    public LeadResponse submit(LeadRequest request, String clientIp, String userAgent, Locale locale) {
        Lead lead = Lead.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .company(request.getCompany())
                .companySize(request.getCompanySize())
                .industry(request.getIndustry())
                .buildType(request.getBuildType())
                .problems(request.getProblems())
                .features(request.getFeatures())
                .status(LeadStatus.NEW)
                .build();

        Lead saved = leadRepository.save(lead);

        List<ScoringResult> results =
            recommendationEngine.recommend(request, locale);

        List<LeadRecommendation> recs = results.stream()
                .map(r -> LeadRecommendation.builder()
                        .lead(saved)
                        .foundationSlug(r.getFoundation().getSlug())
                        .matchScore(r.getScore())
                        .matchReason(r.getReason())
                        .build())
                .collect(Collectors.toList());

        recommendationRepository.saveAll(recs);
        saved.setRecommendations(recs);

        LeadResponse response = mapToResponse(saved);

        // Asynchronous — does not block the HTTP response
        notificationService.sendLeadNotification(response);
        metaConversionsApiService.sendLeadEvent(response,
            request.getFbEventId(), request.getFbp(), request.getFbc(),
            clientIp, userAgent);

        return response;
    }
    
    @Transactional
    public LeadResponse submitQuick(QuickLeadRequest request) {
        Lead lead = Lead.builder()
                .name(request.getName())
                .email(request.getName().toLowerCase()
                    .replaceAll("\\s+", ".") + "@whatsapp.lead")
                .industry(request.getIndustry())
                .source(LeadSource.WHATSAPP_QUICK)
                .status(LeadStatus.NEW)
                .build();

        Lead saved = leadRepository.save(lead);

        // Lighter notification — no PDF, no scoring, just alert
        LeadResponse response = mapToResponse(saved);
        notificationService.sendQuickLeadNotification(response);

        return response;
    }

    public List<LeadResponse> findAll() {
        return leadRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LeadResponse findById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Lead not found: " + id));
        return mapToResponse(lead);
    }

    @Transactional
    public LeadResponse updateStatus(Long id, LeadStatus status) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Lead not found: " + id));
        lead.setStatus(status);
        return mapToResponse(leadRepository.save(lead));
    }

    private LeadResponse mapToResponse(Lead lead) {
        List<RecommendationResponse> recs =
            lead.getRecommendations() == null
                ? List.of()
                : lead.getRecommendations().stream()
                        .map(r -> RecommendationResponse.builder()
                                .foundationSlug(r.getFoundationSlug())
                                .foundationName(
                                    resolveFoundationName(
                                        r.getFoundationSlug()))
                                .matchScore(r.getMatchScore())
                                .matchReason(r.getMatchReason())
                                .build())
                        .collect(Collectors.toList());

        return LeadResponse.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .companySize(lead.getCompanySize())
                .industry(lead.getIndustry())
                .buildType(lead.getBuildType())
                .problems(lead.getProblems())
                .features(lead.getFeatures())
                .status(lead.getStatus().name())
                .createdAt(lead.getCreatedAt())
                .recommendations(recs)
                .build();
    }

    private String resolveFoundationName(String slug) {
        return foundationRepository.findBySlug(slug)
                .map(FoundationEntity::getName)
                .orElse(slug);
    }
}