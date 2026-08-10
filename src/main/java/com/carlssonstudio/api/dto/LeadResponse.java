package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LeadResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String companySize;
    private String industry;
    private String buildType;
    private List<String> problems;
    private List<String> features;
    /** RUNNING or PLANNING — captured by both the questionnaire and Quick Match. */
    private String businessStatus;
    /** Free-text objective; only set when businessStatus is PLANNING. */
    private String goal;
    /** WEBSITE or WHATSAPP_QUICK — which funnel produced this lead. */
    private String source;
    private String status;
    private LocalDateTime createdAt;
    private List<RecommendationResponse> recommendations;
    private OperationalAssessmentResponse operationalAssessment;
}