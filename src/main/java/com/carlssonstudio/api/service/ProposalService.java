package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.entity.*;
import com.carlssonstudio.api.proposal.*;
import com.carlssonstudio.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final LeadRepository leadRepository;
    private final FoundationRepository foundationRepository;
    private final LeadRecommendationRepository recommendationRepository;
    private final ProposalPdfGenerator pdfGenerator;
    private final TimelineCalculator timelineCalculator;

    @Transactional
    public ProposalResponse generate(ProposalRequest request) throws Exception {
        // Load lead
        Lead lead = leadRepository
            .findById(request.getLeadId())
            .orElseThrow(() -> new RuntimeException(
                "Lead not found: " + request.getLeadId()));

        // Load matching recommendation
        LeadRecommendation rec = recommendationRepository
            .findByLeadIdOrderByMatchScoreDesc(lead.getId())
            .stream()
            .filter(r -> r.getFoundationSlug()
                .equals(request.getFoundationSlug()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                "Recommendation not found for foundation: "
                + request.getFoundationSlug()));

        // Calculate timeline and complexity
        int weeks = timelineCalculator.calculateWeeks(
            lead.getFeatures(), lead.getCompanySize());
        ProposalComplexity complexity =
            timelineCalculator.calculateComplexity(
                lead.getFeatures(), lead.getCompanySize());

        // Build DTOs for PDF generator
        LeadResponse leadDto = buildLeadDto(lead, rec);
        RecommendationResponse recDto =
            RecommendationResponse.builder()
                .foundationSlug(rec.getFoundationSlug())
                .foundationName(
                    resolveFoundationName(
                        rec.getFoundationSlug()))
                .matchScore(rec.getMatchScore())
                .matchReason(rec.getMatchReason())
                .build();

        // Generate PDF
        String filePath = pdfGenerator.generate(
            leadDto, recDto, weeks, complexity);

        // Save proposal record
        Proposal proposal = Proposal.builder()
                .lead(lead)
                .foundationSlug(rec.getFoundationSlug())
                .foundationName(recDto.getFoundationName())
                .matchScore(rec.getMatchScore())
                .timelineWeeks(weeks)
                .complexity(complexity)
                .filePath(filePath)
                .status(ProposalStatus.DRAFT)
                .build();

        Proposal saved = proposalRepository.save(proposal);

        log.info("Proposal generated for lead={} foundation={}",
            lead.getId(), rec.getFoundationSlug());

        return mapToResponse(saved);
    }

    public List<ProposalResponse> findByLeadId(Long leadId) {
        return proposalRepository.findByLeadId(leadId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Proposal findEntityById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Proposal not found: " + id));
    }

    private LeadResponse buildLeadDto(
            Lead lead, LeadRecommendation rec) {
        return LeadResponse.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .whatsappOptIn(lead.isWhatsappOptIn())
                .company(lead.getCompany())
                .companySize(lead.getCompanySize())
                .industry(lead.getIndustry())
                .buildType(lead.getBuildType())
                .problems(lead.getProblems())
                .features(lead.getFeatures())
                .status(lead.getStatus().name())
                .createdAt(lead.getCreatedAt())
                .recommendations(List.of(
                    RecommendationResponse.builder()
                        .foundationSlug(rec.getFoundationSlug())
                        .matchScore(rec.getMatchScore())
                        .matchReason(rec.getMatchReason())
                        .build()))
                .build();
    }

    private ProposalResponse mapToResponse(Proposal p) {
        return ProposalResponse.builder()
                .id(p.getId())
                .leadId(p.getLead().getId())
                .foundationSlug(p.getFoundationSlug())
                .foundationName(p.getFoundationName())
                .matchScore(p.getMatchScore())
                .timelineWeeks(p.getTimelineWeeks())
                .complexity(p.getComplexity().name())
                .status(p.getStatus().name())
                .downloadUrl("/api/proposals/"
                    + p.getId() + "/download")
                .createdAt(p.getCreatedAt())
                .build();
    }

    private String resolveFoundationName(String slug) {
        return foundationRepository.findBySlug(slug)
                .map(FoundationEntity::getName)
                .orElse(slug);
    }
}