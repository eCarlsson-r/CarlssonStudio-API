package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationResponse {
    private String foundationSlug;
    private String foundationName;
    private Integer matchScore;
    private String matchReason;
}