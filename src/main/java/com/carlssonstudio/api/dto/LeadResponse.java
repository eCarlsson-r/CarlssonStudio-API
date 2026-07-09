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
    private String company;
    private String companySize;
    private String industry;
    private String buildType;
    private List<String> problems;
    private List<String> features;
    private String status;
    private LocalDateTime createdAt;
    private List<RecommendationResponse> recommendations;
}