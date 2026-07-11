package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoundationPopularityResponse {
    private String foundationSlug;
    private String foundationName;
    private long timesRecommended;
    private long timesTopMatch;
    private double avgMatchScore;
}