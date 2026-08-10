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
                // businessStatus is optional on the wire so an older cached
                // frontend can't 400; assume an operating business when absent.
                .businessStatus(request.getBusinessStatus() != null
                    ? request.getBusinessStatus()
                    : BusinessStatus.RUNNING)
                .goal(request.getGoal())
                .source(LeadSource.WEBSITE)
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
                // Was previously dropped here, which violated the NOT NULL
                // business_status constraint and silently lost every quick lead.
                .businessStatus(request.getBusinessStatus())
                .goal(request.getGoal())
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
        return mapToResponse(lead, Locale.ENGLISH);
    }

    private LeadResponse mapToResponse(Lead lead, Locale locale) {
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
                .businessStatus(lead.getBusinessStatus() == null
                    ? null : lead.getBusinessStatus().name())
                .goal(lead.getGoal())
                .source(lead.getSource() == null ? null : lead.getSource().name())
                .status(lead.getStatus().name())
                .createdAt(lead.getCreatedAt())
                .recommendations(recs)
                .operationalAssessment(buildOperationalAssessment(lead, locale))
                .build();
    }

    private OperationalAssessmentResponse buildOperationalAssessment(Lead lead, Locale locale) {
        String lang = locale != null && "id".equals(locale.getLanguage()) ? "id" : "en";
        List<String> problems = lead.getProblems() == null ? List.of() : lead.getProblems();
        List<String> features = lead.getFeatures() == null ? List.of() : lead.getFeatures();

        int penalty = problems.stream().mapToInt(this::problemWeight).sum()
                + features.stream().mapToInt(this::featureWeight).sum()
                + companySizeWeight(lead.getCompanySize())
                + businessStatusWeight(lead.getBusinessStatus());
        int score = Math.max(35, Math.min(100, 100 - penalty));

        String summary = "RUNNING".equalsIgnoreCase(lead.getBusinessStatus() == null ? null : lead.getBusinessStatus().name())
                ? ("id".equals(lang)
                    ? "Bisnis Anda telah berkembang dan mulai membutuhkan sistem yang lebih terintegrasi agar operasional tetap efisien."
                    : "Your business has grown and now needs a more integrated system so operations stay efficient.")
                : ("id".equals(lang)
                    ? "Bisnis Anda masih dalam tahap pembentukan, sehingga membangun fondasi operasional yang tepat sejak awal akan mengurangi hambatan di kemudian hari."
                    : "Your business is still forming, so building the right operational foundation early will reduce friction later.");

        List<AssessmentFindingResponse> findings = problems.stream()
                .limit(3)
                .map(problem -> AssessmentFindingResponse.builder()
                        .title(problemTitle(problem, lang))
                        .description(problemDescription(problem, lang))
                        .build())
                .collect(Collectors.toList());

        if (findings.isEmpty()) {
            findings = List.of(AssessmentFindingResponse.builder()
                    .title("id".equals(lang) ? "Ada peluang untuk memperkuat fondasi operasional" : "There is room to strengthen the operational foundation")
                    .description("id".equals(lang)
                            ? "Kebutuhan bisnis Anda menunjukkan bahwa sistem yang lebih terstruktur akan membantu pertumbuhan."
                            : "Your business needs suggest that a more structured system will support growth.")
                    .build());
        }

        List<AssessmentPriorityResponse> priorities = List.of(
                AssessmentPriorityResponse.builder()
                        .title("id".equals(lang) ? "Kejelasan alur kerja" : "Workflow clarity")
                        .description("id".equals(lang)
                                ? "Standarisasi bagaimana pekerjaan bergerak di antara orang, alat, dan keputusan."
                                : "Standardize how work moves between people, tools, and decisions.")
                        .icon("workflow")
                        .build(),
                AssessmentPriorityResponse.builder()
                        .title("id".equals(lang) ? "Standarisasi operasional" : "Operational standardization")
                        .description("id".equals(lang)
                                ? "Tentukan aturan, data, dan handoff bersama agar bisnis tumbuh lebih terprediksi."
                                : "Define shared rules, data, and handoffs so the business scales more predictably.")
                        .icon("layers")
                        .build(),
                AssessmentPriorityResponse.builder()
                        .title("id".equals(lang) ? "Implementasi sistem" : "System implementation")
                        .description("id".equals(lang)
                                ? "Perkenalkan fondasi yang mendukung pertumbuhan tanpa membuat tim bergantung pada kerja manual."
                                : "Introduce a foundation that supports growth without making the team dependent on manual work.")
                        .icon("sparkles")
                        .build());

        List<String> recommendationBenefits = problems.stream()
                .map(problem -> recommendationBenefit(problem, lang))
                .filter(benefit -> benefit != null && !benefit.isBlank())
                .limit(5)
                .collect(Collectors.toList());

        if (recommendationBenefits.isEmpty()) {
            recommendationBenefits = List.of(
                    "id".equals(lang) ? "Menyatukan data operasional" : "Unify operational data",
                    "id".equals(lang) ? "Mengurangi ketergantungan pada kerja manual" : "Reduce dependence on manual work");
        }

        return OperationalAssessmentResponse.builder()
                .score(score)
                .summary(summary)
                .findings(findings)
                .priorities(priorities)
                .recommendationBenefits(recommendationBenefits)
                .build();
    }

    private int problemWeight(String problem) {
        return switch (problem) {
            case "Duplicate work" -> 7;
            case "Manual spreadsheets" -> 8;
            case "No dashboard" -> 6;
            case "No reporting" -> 7;
            case "No inventory" -> 7;
            case "No booking" -> 7;
            case "WhatsApp chaos" -> 8;
            default -> 0;
        };
    }

    private int featureWeight(String feature) {
        return switch (feature) {
            case "AI" -> 4;
            case "Dashboard" -> 3;
            case "Inventory" -> 4;
            case "Reports" -> 3;
            case "Scheduling" -> 3;
            default -> 0;
        };
    }

    private int companySizeWeight(String companySize) {
        return switch (companySize) {
            case "1-5" -> 3;
            case "5-20" -> 6;
            case "20-100" -> 10;
            case "100+" -> 14;
            default -> 0;
        };
    }

    private int businessStatusWeight(BusinessStatus businessStatus) {
        return businessStatus == BusinessStatus.RUNNING ? 8 : 4;
    }

    private String problemTitle(String problem, String lang) {
        return switch (problem) {
            case "Duplicate work" -> "id".equals(lang)
                    ? "Masih ada pekerjaan yang berulang dalam operasional harian."
                    : "There is still repetitive work in daily operations.";
            case "Manual spreadsheets" -> "id".equals(lang)
                    ? "Bisnis masih bergantung pada spreadsheet dan follow-up manual."
                    : "The business still depends on spreadsheets and manual follow-up.";
            case "No dashboard" -> "id".equals(lang)
                    ? "Pimpinan belum punya gambaran real-time performa bisnis."
                    : "Leadership does not have a live view of business performance.";
            case "No reporting" -> "id".equals(lang)
                    ? "Pelaporan masih tertunda atau tidak konsisten."
                    : "Reporting is still delayed or inconsistent.";
            case "No inventory" -> "id".equals(lang)
                    ? "Stok berpotensi tidak sinkron."
                    : "Inventory is at risk of becoming disconnected.";
            case "No booking" -> "id".equals(lang)
                    ? "Reservasi masih dilakukan secara manual."
                    : "Reservations are still managed manually.";
            case "WhatsApp chaos" -> "id".equals(lang)
                    ? "Komunikasi masih tersebar di banyak thread chat."
                    : "Communication is still spread across chat threads.";
            default -> "id".equals(lang)
                    ? "Ada peluang untuk memperkuat fondasi operasional"
                    : "There is room to strengthen the operational foundation";
        };
    }

    private String problemDescription(String problem, String lang) {
        return switch (problem) {
            case "Duplicate work" -> "id".equals(lang)
                    ? "Pindah tangan data dan input berulang menciptakan hambatan di tim."
                    : "Manual handoffs and repeated input are creating friction across the team.";
            case "Manual spreadsheets" -> "id".equals(lang)
                    ? "Visibilitas operasional terbatas karena data penting masih tersebar di alat manual."
                    : "Operational visibility is limited because critical data is still spread across manual tools.";
            case "No dashboard" -> "id".equals(lang)
                    ? "Tim belum punya cara sederhana untuk melihat apa yang sedang terjadi secara real time."
                    : "The team is missing a simple way to understand what is happening in real time.";
            case "No reporting" -> "id".equals(lang)
                    ? "Pengambilan keputusan lebih lambat karena laporan belum tersedia tepat saat dibutuhkan."
                    : "Decision-making is slower because reports are not yet available when the team needs them.";
            case "No inventory" -> "id".equals(lang)
                    ? "Pergerakan stok lebih sulit dilacak sehingga menimbulkan risiko di pemesanan dan fulfillment."
                    : "Stock movement is harder to track, which creates risk for ordering and fulfillment.";
            case "No booking" -> "id".equals(lang)
                    ? "Alur booking rentan terhadap langkah yang terlewat dan pengalaman pelanggan yang tidak konsisten."
                    : "Booking flow is vulnerable to missed steps and inconsistent customer experience.";
            case "WhatsApp chaos" -> "id".equals(lang)
                    ? "Pesanan, permintaan, dan follow-up terlalu mudah hilang di aplikasi chat."
                    : "Orders, requests, and follow-up are too easy to lose inside messaging apps.";
            default -> "id".equals(lang)
                    ? "Kebutuhan bisnis Anda menunjukkan bahwa sistem yang lebih terstruktur akan membantu pertumbuhan."
                    : "Your business needs suggest that a more structured system will support growth.";
        };
    }

    private String recommendationBenefit(String problem, String lang) {
        return switch (problem) {
            case "Duplicate work" -> "id".equals(lang)
                    ? "Mengurangi input berulang di seluruh tim"
                    : "Reduce repetitive input across the team";
            case "Manual spreadsheets" -> "id".equals(lang)
                    ? "Mengurangi ketergantungan pada spreadsheet manual"
                    : "Move away from manual spreadsheets";
            case "No dashboard" -> "id".equals(lang)
                    ? "Menyediakan gambaran performa bisnis yang dibagi bersama"
                    : "Create a shared view of business performance";
            case "No reporting" -> "id".equals(lang)
                    ? "Memberikan pelaporan dan visibilitas real-time"
                    : "Provide real-time reporting and visibility";
            case "No inventory" -> "id".equals(lang)
                    ? "Menjaga data stok dan inventori tetap sinkron"
                    : "Keep inventory and stock data aligned";
            case "No booking" -> "id".equals(lang)
                    ? "Memudahkan proses reservasi dan alur pelanggan"
                    : "Streamline reservations and customer flow";
            case "WhatsApp chaos" -> "id".equals(lang)
                    ? "Mengumpulkan komunikasi pelanggan ke satu alur yang terstruktur"
                    : "Bring customer communication into one structured flow";
            default -> null;
        };
    }

    private String resolveFoundationName(String slug) {
        return foundationRepository.findBySlug(slug)
                .map(FoundationEntity::getName)
                .orElse(slug);
    }
}