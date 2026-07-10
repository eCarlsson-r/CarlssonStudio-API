package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProposalResponse {
    private Long id;
    private Long leadId;
    private String foundationSlug;
    private String foundationName;
    private Integer matchScore;
    private Integer timelineWeeks;
    private String complexity;
    private String status;
    private String downloadUrl;
    private LocalDateTime createdAt;
}